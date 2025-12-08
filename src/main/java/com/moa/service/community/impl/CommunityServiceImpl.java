package com.moa.service.community.impl;

import com.moa.domain.enums.PushCodeType;
import com.moa.dao.community.CommunityDao;
import com.moa.domain.Community;
import com.moa.dto.community.request.*;
import com.moa.dto.community.response.*;
import com.moa.dto.push.request.TemplatePushRequest;
import com.moa.service.community.CommunityService;
import com.moa.service.push.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {
    
    private final CommunityDao communityDao;
    private final PushService pushService;
    
    @Value("${app.upload.inquiry.image-dir}")
    private String inquiryImageDir;
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");
    
    @Override
    public PageResponse<NoticeResponse> getNoticeList(int page, int size) {
        int offset = (page - 1) * size;
        List<Community> noticeList = communityDao.getNoticeList(offset, size);
        int totalCount = communityDao.getNoticeTotalCount();
        
        List<NoticeResponse> responseList = noticeList.stream()
                .map(NoticeResponse::fromEntity)
                .collect(Collectors.toList());
        
        return new PageResponse<>(responseList, page, size, totalCount);
    }
    
    @Override
    @Transactional
    public NoticeResponse getNotice(Integer communityId) {
        communityDao.incrementViewCount(communityId);
        Community community = communityDao.getNotice(communityId);
        return NoticeResponse.fromEntity(community);
    }
    
    @Override
    @Transactional
    public void addNotice(NoticeRequest request) {
        Community community = request.toEntity();
        communityDao.addNotice(community);
    }
    
    @Override
    @Transactional
    public void updateNotice(Integer communityId, NoticeRequest request) {
        Community community = request.toEntity();
        community.setCommunityId(communityId);
        communityDao.updateNotice(community);
    }
    
    @Override
    public PageResponse<NoticeResponse> searchNotice(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<Community> noticeList = communityDao.searchNotice(keyword, offset, size);
        int totalCount = communityDao.searchNoticeTotalCount(keyword);
        
        List<NoticeResponse> responseList = noticeList.stream()
                .map(NoticeResponse::fromEntity)
                .collect(Collectors.toList());
        
        return new PageResponse<>(responseList, page, size, totalCount);
    }
    
    @Override
    public PageResponse<FaqResponse> getFaqList(int page, int size) {
        int offset = (page - 1) * size;
        List<Community> faqList = communityDao.getFaqList(offset, size);
        int totalCount = communityDao.getFaqTotalCount();
        
        List<FaqResponse> responseList = faqList.stream()
                .map(FaqResponse::fromEntity)
                .collect(Collectors.toList());
        
        return new PageResponse<>(responseList, page, size, totalCount);
    }
    
    @Override
    public FaqResponse getFaq(Integer communityId) {
        Community community = communityDao.getFaq(communityId);
        return FaqResponse.fromEntity(community);
    }
    
    @Override
    @Transactional
    public void addFaq(FaqRequest request) {
        Community community = request.toEntity();
        communityDao.addFaq(community);
    }
    
    @Override
    @Transactional
    public void updateFaq(Integer communityId, FaqRequest request) {
        Community community = request.toEntity();
        community.setCommunityId(communityId);
        communityDao.updateFaq(community);
    }
    
    @Override
    public PageResponse<FaqResponse> searchFaq(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<Community> faqList = communityDao.searchFaq(keyword, offset, size);
        int totalCount = communityDao.searchFaqTotalCount(keyword);
        
        List<FaqResponse> responseList = faqList.stream()
                .map(FaqResponse::fromEntity)
                .collect(Collectors.toList());
        
        return new PageResponse<>(responseList, page, size, totalCount);
    }
    
    @Override
    public PageResponse<InquiryResponse> getMyInquiryList(String userId, int page, int size) {
        int offset = (page - 1) * size;
        List<Community> inquiryList = communityDao.getMyInquiryList(userId, offset, size);
        int totalCount = communityDao.getMyInquiryTotalCount(userId);
        
        List<InquiryResponse> responseList = inquiryList.stream()
                .map(InquiryResponse::fromEntity)
                .collect(Collectors.toList());
        
        return new PageResponse<>(responseList, page, size, totalCount);
    }
    
    @Override
    public PageResponse<InquiryResponse> getInquiryList(int page, int size) {
        int offset = (page - 1) * size;
        List<Community> inquiryList = communityDao.getInquiryList(offset, size);
        int totalCount = communityDao.getInquiryTotalCount();
        
        List<InquiryResponse> responseList = inquiryList.stream()
                .map(InquiryResponse::fromEntity)
                .collect(Collectors.toList());
        
        return new PageResponse<>(responseList, page, size, totalCount);
    }
    
    @Override
    public InquiryResponse getInquiry(Integer communityId) {
        Community community = communityDao.getInquiry(communityId);
        return InquiryResponse.fromEntity(community);
    }
    
    @Override
    @Transactional
    public void addInquiry(String userId, Integer communityCodeId, String title, String content, MultipartFile file) {
        String fileOriginal = null;
        String fileUuid = null;
        
        if (file != null && !file.isEmpty()) {
            validateFile(file);
            
            fileOriginal = file.getOriginalFilename();
            String extension = getFileExtension(fileOriginal);
            fileUuid = UUID.randomUUID().toString() + "." + extension;
            
            saveFile(file, fileUuid);
        }
        
        InquiryRequest request = InquiryRequest.builder()
                .userId(userId)
                .communityCodeId(communityCodeId)
                .title(title)
                .content(content)
                .fileOriginal(fileOriginal)
                .fileUuid(fileUuid)
                .build();
        
        Community community = request.toEntity();
        communityDao.addInquiry(community);
    }
    
    @Override
    @Transactional
    public void addAnswer(AnswerRequest request) {
        communityDao.addAnswer(request.getCommunityId(), request.getAnswerContent());
        
        Community inquiry = communityDao.getInquiry(request.getCommunityId());
        
        TemplatePushRequest pushRequest = TemplatePushRequest.builder()
                .receiverId(inquiry.getUserId())
                .pushCode(PushCodeType.INQUIRY_ANSWER.getCode())
                .params(Map.of("nickname", inquiry.getNickname()))
                .moduleId(String.valueOf(request.getCommunityId()))
                .moduleType(PushCodeType.INQUIRY_ANSWER.getModuleType())
                .build();
        
        pushService.sendTemplatePush(pushRequest);
    }
    
    @Override
    @Transactional
    public void updateAnswer(AnswerRequest request) {
        communityDao.updateAnswer(request.getCommunityId(), request.getAnswerContent());
    }
    
    @Override
    public Resource getInquiryImage(String fileUuid) {
        try {
            Path filePath = Paths.get(inquiryImageDir).resolve(fileUuid).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + fileUuid);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 경로 오류: " + fileUuid, e);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 10MB 이하만 가능합니다.");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("JPG, PNG 파일만 업로드 가능합니다.");
        }
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
    
    private void saveFile(MultipartFile file, String fileUuid) {
        try {
            Path uploadPath = Paths.get(inquiryImageDir);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileUuid);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }
}