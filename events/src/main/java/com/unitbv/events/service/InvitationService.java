package com.unitbv.events.service;

import com.unitbv.events.response.InvitationDataResponse;

public interface InvitationService {
	InvitationDataResponse getInvitationByCode(String invitationCode);

}
