package com.vrms.ngo_posting_service.service.impl;

import com.vrms.ngo_posting_service.dto.CreateNgoPostRequest;
import com.vrms.ngo_posting_service.dto.NgoPostResponse;
import com.vrms.ngo_posting_service.dto.UpdateNgoPostRequest;
import com.vrms.ngo_posting_service.entity.NgoPost;
import com.vrms.ngo_posting_service.entity.PostStatus;
import com.vrms.ngo_posting_service.exception.ForbiddenException;
import com.vrms.ngo_posting_service.exception.ResourceNotFoundException;
import com.vrms.ngo_posting_service.repository.NgoPostRepository;
import com.vrms.ngo_posting_service.service.NgoPostService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NgoPostServiceImpl implements NgoPostService {

    private final NgoPostRepository repository;

    @Override
    public NgoPostResponse createPost(CreateNgoPostRequest request, Long ngoId) {
        NgoPost post = new NgoPost();
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setDomain(request.getDomain());
        post.setLocation(request.getLocation());
        post.setCity(request.getCity());
        post.setState(request.getState());
        post.setCountry(request.getCountry());
        post.setPincode(request.getPincode());
        post.setEffortRequired(request.getEffortRequired());
        post.setVolunteersNeeded(request.getVolunteersNeeded());
        post.setStartDate(request.getStartDate());
        post.setEndDate(request.getEndDate());
        post.setNgoId(ngoId);
        post.setContactEmail(request.getContactEmail());
        post.setContactPhone(request.getContactPhone());
        post.setStatus(PostStatus.ACTIVE);

        NgoPost saved = repository.save(post);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NgoPostResponse getPostById(Long id) {
        NgoPost post = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return mapToResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NgoPostResponse> getAllPosts(Pageable pageable) {
        return repository.findByStatus(PostStatus.ACTIVE, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NgoPostResponse> getPostsByNgoId(Long ngoId, Pageable pageable) {
        return repository.findByNgoId(ngoId, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NgoPostResponse> getPostsByDomain(String domain, Pageable pageable) {
        return repository.findByDomain(domain, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NgoPostResponse> getPostsByCity(String city, Pageable pageable) {
        return repository.findByCity(city, pageable)
            .map(this::mapToResponse);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<NgoPostResponse> getPostsByPincode(String pincode, Pageable pageable) {
        return repository.findByPincode(pincode, pageable)
            .map(this::mapToResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<NgoPostResponse> searchPosts(String keyword, Pageable pageable) {
        return repository.searchPosts(keyword, PostStatus.ACTIVE, pageable)
            .map(this::mapToResponse);
    }

    @Override
    public NgoPostResponse updatePost(Long id, UpdateNgoPostRequest request, Long userId,String role) {
        NgoPost post = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

          // ADMIN can update any post, NGO can only update their own
    if ("NGO".equals(role) && !post.getNgoId().equals(userId)) {
        throw new ForbiddenException("You are not authorized to update this posting");
    }
        // Update fields if provided
        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getDescription() != null) post.setDescription(request.getDescription());
        if (request.getDomain() != null) post.setDomain(request.getDomain());
        if (request.getLocation() != null) post.setLocation(request.getLocation());
        if (request.getCity() != null) post.setCity(request.getCity());
        if (request.getPincode() != null) post.setPincode(request.getPincode());
        if (request.getState() != null) post.setState(request.getState());
        if (request.getCountry() != null) post.setCountry(request.getCountry());
        if (request.getEffortRequired() != null) post.setEffortRequired(request.getEffortRequired());
        if (request.getVolunteersNeeded() != null) post.setVolunteersNeeded(request.getVolunteersNeeded());
        if (request.getStartDate() != null) post.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) post.setEndDate(request.getEndDate());
        if (request.getContactEmail() != null) post.setContactEmail(request.getContactEmail());
        if (request.getContactPhone() != null) post.setContactPhone(request.getContactPhone());
        if (request.getStatus() != null) post.setStatus(request.getStatus());

        NgoPost updated = repository.save(post);
        return mapToResponse(updated);
    }

        @Override
        public void deletePost(Long id, Long userId,String role) {
            NgoPost post = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

            // ADMIN can delete any post, NGO can only delete their own
            if ("NGO".equals(role) && !post.getNgoId().equals(userId)) {
                throw new ForbiddenException("You are not authorized to delete this posting");
            }
            repository.delete(post);
        }
@Override
    @Transactional
    public void registerVolunteer(Long postingId, Long volunteerId) {
        NgoPost post = repository.findById(postingId)
            .orElseThrow(() -> new ResourceNotFoundException("Posting not found with id: " + postingId));

        Set<Long> registeredVolunteers = post.getVolunteersRegistered();
        if (registeredVolunteers == null) {
            registeredVolunteers = new HashSet<>();
            post.setVolunteersRegistered(registeredVolunteers);
        }

        if (registeredVolunteers.contains(volunteerId)) {
            throw new IllegalStateException("Volunteer already registered");
        }

        if (post.getVolunteersSpotLeft() == null) {
            post.setVolunteersSpotLeft(post.getVolunteersNeeded());
        }

        if (post.getVolunteersSpotLeft() <= 0) {
            throw new IllegalStateException("No spots left for this posting");
        }

        registeredVolunteers.add(volunteerId);
        post.setVolunteersSpotLeft(post.getVolunteersSpotLeft() - 1);

        repository.save(post);
    }


    @Override
    @Transactional
    public void unregisterVolunteer(Long postingId, Long volunteerId) {
        log.info(" Unregistering volunteer {} from posting {}", volunteerId, postingId);
        
        NgoPost post = repository.findById(postingId)
            .orElseThrow(() -> new ResourceNotFoundException("Posting not found with id: " + postingId));

        Set<Long> registeredVolunteers = post.getVolunteersRegistered();
        
        // Check if volunteer is registered
        if (registeredVolunteers == null || !registeredVolunteers.contains(volunteerId)) {
            log.warn(" Volunteer {} not registered for posting {}", volunteerId, postingId);
            throw new IllegalStateException("Volunteer not registered for this posting");
        }

        // Remove volunteer from registered list
        registeredVolunteers.remove(volunteerId);
        
        // Increase available spot
        if (post.getVolunteersSpotLeft() != null) {
            post.setVolunteersSpotLeft(post.getVolunteersSpotLeft() + 1);
            log.debug(" Increased volunteersSpotLeft: {} -> {}", 
                    post.getVolunteersSpotLeft() - 1, post.getVolunteersSpotLeft());
        }

        repository.save(post);
        log.info(" Successfully unregistered volunteer {} from posting {}", volunteerId, postingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getVolunteersForPosting(Long postingId) {
        log.info("📋 Getting volunteers for posting {}", postingId);
        
        NgoPost post = repository.findById(postingId)
            .orElseThrow(() -> new ResourceNotFoundException("Posting not found with id: " + postingId));

        Set<Long> registeredVolunteers = post.getVolunteersRegistered();
        
        if (registeredVolunteers == null || registeredVolunteers.isEmpty()) {
            log.info("📭 No volunteers registered for posting {}", postingId);
            return new ArrayList<>();
        }
        
        List<Long> volunteerIds = new ArrayList<>(registeredVolunteers);
        log.info("✅ Found {} volunteers for posting {}", volunteerIds.size(), postingId);
        
        return volunteerIds;
    }

    private NgoPostResponse mapToResponse(NgoPost post) {
        NgoPostResponse response = new NgoPostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setDescription(post.getDescription());
        response.setDomain(post.getDomain());
        response.setLocation(post.getLocation());
        response.setCity(post.getCity());
        response.setState(post.getState());
        response.setCountry(post.getCountry());
        response.setPincode(post.getPincode());  // map pincode
        response.setEffortRequired(post.getEffortRequired());
        response.setVolunteersNeeded(post.getVolunteersNeeded());
        response.setVolunteersSpotLeft(post.getVolunteersSpotLeft());  // map spots left
        response.setVolunteersRegistered(post.getVolunteersRegistered());  // map volunteer IDs
        response.setStartDate(post.getStartDate());
        response.setEndDate(post.getEndDate());
        response.setNgoId(post.getNgoId());
        response.setContactEmail(post.getContactEmail());
        response.setContactPhone(post.getContactPhone());
        response.setStatus(post.getStatus());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        return response;
    }
}

