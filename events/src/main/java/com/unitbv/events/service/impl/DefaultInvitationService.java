package com.unitbv.events.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.unitbv.events.dao.InvitationDao;
import com.unitbv.events.data.InvitationData;
import com.unitbv.events.data.InvitationFileData;
import com.unitbv.events.model.Invitation;
import com.unitbv.events.response.InvitationDataResponse;
import com.unitbv.events.service.InvitationService;
import com.unitbv.events.util.EntityDAOImplFactory;

public class DefaultInvitationService implements InvitationService {
	private static final String PERSISTENCE_UNIT_NAME = "events";
	private EntityDAOImplFactory entityDAOImplFactory;
	private InvitationDao invitationDao;

	public DefaultInvitationService() {
		invitationDao = entityDAOImplFactory.createNewInvitationDao(PERSISTENCE_UNIT_NAME);
	}

	@Override
	public InvitationDataResponse getInvitationByCode(String invitationCode) {
		InvitationDataResponse invitationDataResponse = new InvitationDataResponse();
		Invitation inv = invitationDao.findById(Integer.valueOf(invitationCode));
		if (Objects.isNull(inv)) {
			invitationDataResponse.setStatusCode("404");
		} else {
			invitationDataResponse.setStatusCode("200");
			List<InvitationData> invitations = new ArrayList();
			InvitationData invitationData = new InvitationData();
			invitationData.setCreationDate(inv.getCreationDate());
			invitationData.setDescription(inv.getDescription());
			invitationData.setEventDate(inv.getEvent().getDate());
			invitationData.setEventName(inv.getEvent().getName());
			if (Objects.isNull(inv.getIsAccepted())) {
				invitationData.setIsAccepted("Wait");
			} else {
				invitationData.setIsAccepted(inv.getIsAccepted() ? "Yes" : "No");
			}
			invitationData.setReference("Ref: " + inv.getInvitationId());
			InvitationFileData invitationFile = new InvitationFileData();
			invitationFile.setExtension(inv.getInvitationfile().getExtension());
			invitationFile.setFileData(inv.getInvitationfile().getFileData());
			invitationData.setInvitationFile(invitationFile);
			invitations.add(invitationData);
			invitationDataResponse.setInvitations(invitations);
		}
		return invitationDataResponse;
	}

}
