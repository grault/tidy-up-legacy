package com.kodekonveyor.tidyup;

import org.springframework.hateoas.ResourceSupport;

import lombok.NoArgsConstructor;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@NoArgsConstructor
public class TidyUserResource extends ResourceSupport {
	private static final String USER_WORKREQUESTS = "user-workrequests";
	private static final String ALL_USERS = "all-users";

	public TidyUserResource(final TidyUser user) {
		final long id = user.getId();
		add(linkTo(TidyUserController.class).withRel(ALL_USERS));
		add(linkTo(methodOn(WorkRequestsController.class).all(id)).withRel(USER_WORKREQUESTS));
		add(linkTo(methodOn(TidyUserController.class).get(id)).withSelfRel());
	}
}