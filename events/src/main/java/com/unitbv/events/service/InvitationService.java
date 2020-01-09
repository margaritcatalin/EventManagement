package com.unitbv.events.service;

import java.util.List;

import com.unitbv.events.model.Invitation;
import com.unitbv.events.response.InvitationDataResponse;

public interface InvitationService {
	InvitationDataResponse getInvitationByCode(String invitationCode);
	List<Invitation> readAllByUserEmail(String email);

}
