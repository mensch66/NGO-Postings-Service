package com.vrms.ngo_posting_service.service;

import com.vrms.ngo_posting_service.dto.CreateNgoPostRequest;
import com.vrms.ngo_posting_service.dto.NgoPostResponse;
import com.vrms.ngo_posting_service.dto.UpdateNgoPostRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NgoPostService {
    NgoPostResponse createPost(CreateNgoPostRequest request, Long ngoId);
    NgoPostResponse getPostById(Long id);
    Page<NgoPostResponse> getAllPosts(Pageable pageable);
    Page<NgoPostResponse> getPostsByNgoId(Long ngoId, Pageable pageable);
    Page<NgoPostResponse> getPostsByDomain(String domain, Pageable pageable);
    Page<NgoPostResponse> getPostsByCity(String city, Pageable pageable);
    Page<NgoPostResponse> getPostsByPincode(String pincode, Pageable pageable);
    Page<NgoPostResponse> searchPosts(String keyword, Pageable pageable);
    NgoPostResponse updatePost(Long id, UpdateNgoPostRequest request, Long ngoId,String role);
    void deletePost(Long id, Long ngoId,String role);
    void registerVolunteer(Long postingId, Long volunteerId);
    void unregisterVolunteer(Long postingId, Long volunteerId);
    List<Long> getVolunteersForPosting(Long postingId);
}
