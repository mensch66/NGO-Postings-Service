package com.vrms.ngo_posting_service.controller;

import com.vrms.ngo_posting_service.dto.CreateNgoPostRequest;
import com.vrms.ngo_posting_service.dto.NgoPostResponse;
import com.vrms.ngo_posting_service.dto.UpdateNgoPostRequest;
import com.vrms.ngo_posting_service.dto.VolunteerDTO;
import com.vrms.ngo_posting_service.exception.ForbiddenException;
import com.vrms.ngo_posting_service.service.NgoPostService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174", "http://localhost:8080"},
    allowCredentials = "true",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@Slf4j
@RestController
@RequestMapping("/api/v1/postings")
@RequiredArgsConstructor
public class NgoPostController {

    private final NgoPostService service;
    private final RestTemplate restTemplate;
    @Value("${user.service.url:http://localhost:8082}")
    private String userServiceUrl;


    /**
     * Create a new posting (NGO or ADMIN only)
     */
    @PostMapping
    public ResponseEntity<NgoPostResponse> createPost(
            @Valid @RequestBody CreateNgoPostRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        String role = (String) httpRequest.getAttribute("role");
        String username = (String) httpRequest.getAttribute("username");

        log.info("CREATE POST - User: {}, UserId: {}, Role: {}", username, userId, role);

        // Only NGO and ADMIN can create postings
        if (!("NGO".equals(role) || "ADMIN".equals(role))) {
            log.warn("FORBIDDEN: User {} with role {} attempted to create posting", username, role);
            throw new ForbiddenException("Only NGO and ADMIN users can create postings");
        }

        NgoPostResponse response = service.createPost(request, userId);
        log.info("POST created successfully with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get posting by ID (all authenticated users can view)
     */
    @GetMapping("/{id}")
    public ResponseEntity<NgoPostResponse> getPost(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        log.debug("GET POST - PostId: {}, UserId: {}", id, userId);

        return ResponseEntity.ok(service.getPostById(id));
    }

    /**
     * Get all active postings (paginated)
     */
    @GetMapping
    public ResponseEntity<Page<NgoPostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        String role = (String) httpRequest.getAttribute("role");

        log.debug("GET ALL POSTS - UserId: {}, Role: {}, Page: {}, Size: {}", userId, role, page, size);

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(service.getAllPosts(pageable));
    }

    /**
     * Get postings by NGO ID (ADMIN can view all, NGO can only view their own)
     */
    @GetMapping("/ngo/{ngoId}")
    public ResponseEntity<Page<NgoPostResponse>> getPostsByNgo(
            @PathVariable Long ngoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        String role = (String) httpRequest.getAttribute("role");

        log.debug("GET NGO POSTS - NGO_ID: {}, UserId: {}, Role: {}", ngoId, userId, role);

        // NGO can only view their own postings
        if ("NGO".equals(role) && !ngoId.equals(userId)) {
            log.warn("FORBIDDEN: NGO {} attempted to view postings of NGO {}", userId, ngoId);
            throw new ForbiddenException("You can only view your own postings");
        }

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getPostsByNgoId(ngoId, pageable));
    }

    /**
     * Get postings by domain
     */
    @GetMapping("/domain/{domain}")
    public ResponseEntity<Page<NgoPostResponse>> getPostsByDomain(
            @PathVariable String domain,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        log.debug("GET POSTS BY DOMAIN - Domain: {}, UserId: {}", domain, userId);

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getPostsByDomain(domain, pageable));
    }

    /**
     * Update posting (NGO can update own, ADMIN can update any)
     */
    @PutMapping("/{id}")
    public ResponseEntity<NgoPostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNgoPostRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        String role = (String) httpRequest.getAttribute("role");
        String username = (String) httpRequest.getAttribute("username");

        log.info("UPDATE POST - PostId: {}, UserId: {}, Role: {}", id, userId, role);

        return ResponseEntity.ok(service.updatePost(id, request, userId, role));
    }

    /**
     * Delete posting (NGO can delete own, ADMIN can delete any)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        String role = (String) httpRequest.getAttribute("role");

        log.info("DELETE POST - PostId: {}, UserId: {}, Role: {}", id, userId, role);

        service.deletePost(id, userId, role);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postingId}/register/{volunteerId}")
    public ResponseEntity<Void> registerVolunteer(
        @PathVariable Long postingId,
        @PathVariable Long volunteerId,
        HttpServletRequest httpRequest) {

    Long userId = (Long) httpRequest.getAttribute("userId");
    String role = (String) httpRequest.getAttribute("role");


    // Optional: Ensure the authenticated user is the volunteer or has admin rights
    if (!volunteerId.equals(userId) && !"ADMIN".equals(role)) {
        throw new ForbiddenException("You can only register yourself or have ADMIN rights");
    }

    log.info("REGISTER VOLUNTEER - PostingId: {}, VolunteerId: {}, UserId: {}, Role: {}", 
        postingId, volunteerId, userId, role);

    service.registerVolunteer(postingId, volunteerId);
    return ResponseEntity.ok().build();
}
@DeleteMapping("/{postingId}/unregister/{volunteerId}")
public ResponseEntity<Void> unregisterVolunteer(
    @PathVariable Long postingId,
    @PathVariable Long volunteerId,
    HttpServletRequest httpRequest) {

Long userId = (Long) httpRequest.getAttribute("userId");
String role = (String) httpRequest.getAttribute("role");


// Optional: Ensure the authenticated user is the volunteer or has admin rights
if (!volunteerId.equals(userId) && !"ADMIN".equals(role)) {
    throw new ForbiddenException("You can only register yourself or have ADMIN rights");
}

log.info("➖ Unregistering volunteer {} from posting {}", volunteerId, postingId);
        service.unregisterVolunteer(postingId, volunteerId);
        return ResponseEntity.ok().build();
}

    @GetMapping("/{postingId}/volunteers")
    public ResponseEntity<List<Long>> getVolunteersForPosting(  // or Map<String, Object> or Long
                                                                        @PathVariable Long postingId,
                                                                        HttpServletRequest httpRequest) {

        // Implementation
        return ResponseEntity.ok(service.getVolunteersForPosting(postingId));
    }

    /**
     * Get volunteer details for NGO's own postings
     * Add this method to your NgoPostController class
     */
    @GetMapping("/{postingId}/volunteers/{volunteerId}/details")
    public ResponseEntity<Object> getVolunteerDetailsForPosting(
            @PathVariable Long postingId,
            @PathVariable Long volunteerId,
            HttpServletRequest httpRequest) {

        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            String role = (String) httpRequest.getAttribute("role");
            String authHeader = httpRequest.getHeader("Authorization");

            log.info("GET VOLUNTEER DETAILS - PostingId: {}, VolunteerId: {}, NGO UserId: {}, Role: {}",
                    postingId, volunteerId, userId, role);

            // Verify NGO owns this posting or is ADMIN
            if ("NGO".equals(role)) {
                // Get posting details to verify ownership
                try {
                    NgoPostResponse posting = service.getPostById(postingId);
                    if (!posting.getNgoId().equals(userId)) {
                        log.warn("FORBIDDEN: NGO {} attempted to access volunteer details for posting {} owned by NGO {}",
                                userId, postingId, posting.getNgoId());
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("You can only access volunteer details for your own postings");
                    }
                } catch (Exception e) {
                    log.error("Error verifying posting ownership: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Posting not found");
                }

                // Verify volunteer is registered for this posting
                List<Long> volunteersForPosting = service.getVolunteersForPosting(postingId);
                if (!volunteersForPosting.contains(volunteerId)) {
                    log.warn("FORBIDDEN: Volunteer {} is not registered for posting {}", volunteerId, postingId);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Volunteer is not registered for this posting");
                }
            }

            // Forward request to User Service to get volunteer details
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            String volunteerEndpoint = userServiceUrl + "/api/v1/users/volunteers/" + volunteerId + "/ngo-access";

            log.info("Forwarding request to User Service: {}", volunteerEndpoint);

            ResponseEntity<Object> response = restTemplate.exchange(
                    volunteerEndpoint,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            log.info("Successfully retrieved volunteer details for volunteer {} from User Service", volunteerId);
            return response;

        } catch (Exception e) {
            log.error("Error fetching volunteer details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching volunteer details: " + e.getMessage());
        }
    }

}

