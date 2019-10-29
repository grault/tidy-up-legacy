package com.kodekonveyor.tidyup;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/users/{userId}/workrequests")
public class WorkRequestsController {
	
	@Autowired
	private TidyUserRepository tidyUserRepository;
	
	@Autowired
	private WorkRequestRepository workRequestRepository;
	
	@GetMapping
	  public ResponseEntity<Resources<WorkRequestResource>> all(@PathVariable final long userId) {
	    final List<WorkRequestResource> collection = getWorkRequestsForUser(userId);
	    final Resources<WorkRequestResource> resources = new Resources<>(collection);
	    final String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
	    resources.add(new Link(uriString, "self"));
	    return ResponseEntity.ok(resources);
	  }

	  private List<WorkRequestResource> getWorkRequestsForUser(final long userId) {
	    return tidyUserRepository
	        .findById(userId)
	        .map(
	            p ->
	                p.getWorkRequests()
	                    .stream()
	                    .map(WorkRequestResource::new)
	                    .collect(Collectors.toList()))
	        .orElseThrow(() -> new TidyUserNotFoundException(userId));
	  }

	  private void validateUser(final long userId) {
	    tidyUserRepository.findById(userId).orElseThrow(() -> new TidyUserNotFoundException(userId));
	  }

	  @GetMapping("/{workRequestId}")
	  public ResponseEntity<WorkRequestResource> get(
	      @PathVariable final long userId, @PathVariable final long workRequestId) {
	    return tidyUserRepository
	        .findById(userId)
	        .map(
	            p ->
	                p.getWorkRequests()
	                    .stream()
	                    .filter(m -> m.getId().equals(workRequestId))
	                    .findAny()
	                    .map(m -> ResponseEntity.ok(new WorkRequestResource(m)))
	                    .orElseThrow(() -> new WorkRequestNotFoundException(workRequestId)))
	        .orElseThrow(() -> new TidyUserNotFoundException(userId));
	  }

	  @PostMapping
	  public ResponseEntity<WorkRequestResource> post(
	      @PathVariable final long userId, @RequestBody final WorkRequestDto inputRequest) {
	    return tidyUserRepository
	        .findById(userId)
	        .map(
	            p -> {
	            	WorkRequest r = new WorkRequest();
	            	r.setCity(inputRequest.getCity());
	            	r.setDescription(inputRequest.getDescription());
	            	r.setUser(p);
	            	WorkRequest request = workRequestRepository.save(r);
	              final URI uri = createPostUri(request);
	              return ResponseEntity.created(uri).body(new WorkRequestResource(request));
	            })
	        .orElseThrow(() -> new TidyUserNotFoundException(userId));
	  }

	  private URI createPostUri(final WorkRequest workRequest) {
	    return MvcUriComponentsBuilder.fromController(getClass())
	        .path("/{workRequestId}")
	        .buildAndExpand(workRequest.getUser().getId(), workRequest.getId())
	        .toUri();
	  }

	  @PutMapping("/{workRequestId}")
	  public ResponseEntity<WorkRequestResource> put(
	      @PathVariable final long userId,
	      @PathVariable final long workRequestId,
	      @RequestBody final WorkRequestDto inputRequest) {
	    return tidyUserRepository
	        .findById(userId)
	        .map(
	            p -> {
	            	WorkRequest r = new WorkRequest();
	            	r.setCity(inputRequest.getCity());
	            	r.setDescription(inputRequest.getDescription());
	            	r.setUser(p);
	            	r.setId(workRequestId);
	            	WorkRequest request = workRequestRepository.save(r);
	              final URI uri =
	                  URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
	              return ResponseEntity.created(uri).body(new WorkRequestResource(request));
	            })
	        .orElseThrow(() -> new TidyUserNotFoundException(userId));
	  }

	  @DeleteMapping("/{workRequestId}")
	  public ResponseEntity<?> delete(
	      @PathVariable final long userId, @PathVariable final long workRequestId) {
	    return tidyUserRepository
	        .findById(userId)
	        .map(
	            p ->
	                p.getWorkRequests()
	                    .stream()
	                    .filter(m -> m.getId().equals(workRequestId))
	                    .findAny()
	                    .map(
	                        m -> {
	                          workRequestRepository.delete(m);
	                          return ResponseEntity.noContent().build();
	                        })
	                    .orElseThrow(() -> new WorkRequestNotFoundException(workRequestId)))
	        .orElseThrow(() -> new TidyUserNotFoundException(userId));
	}

}
