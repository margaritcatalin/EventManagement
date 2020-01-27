package com.unitbv.events.service;

import java.util.List;

import com.unitbv.events.model.Invitation;
import com.unitbv.events.request.AcceptInvitationRequest;
import com.unitbv.events.request.SendInvitationRequest;
import com.unitbv.events.response.InvitationDataResponse;
import com.unitbv.events.response.SimpleResponse;

public interface InvitationService {
	InvitationDataResponse getInvitationByCode(String invitationCode);

	List<Invitation> readAllByUserEmail(String email);

	InvitationDataResponse getAllInvitationForUser(String currentUserEmail);

	boolean sendInvitation(SendInvitationRequest sendInvitationRequest);

	SimpleResponse acceptInvitation(AcceptInvitationRequest request);
}
