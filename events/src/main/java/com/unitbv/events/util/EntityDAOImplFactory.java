package com.unitbv.events.util;

import com.unitbv.events.dao.EventDao;
import com.unitbv.events.dao.InvitationDao;
import com.unitbv.events.dao.InvitationFileDao;
import com.unitbv.events.dao.NotificationDao;
import com.unitbv.events.dao.RoleDao;
import com.unitbv.events.dao.UserDao;
import com.unitbv.events.dao.impl.DefaultEventDao;
import com.unitbv.events.dao.impl.DefaultInvitationDao;
import com.unitbv.events.dao.impl.DefaultInvitationFileDao;
import com.unitbv.events.dao.impl.DefaultNotificationDao;
import com.unitbv.events.dao.impl.DefaultRoleDao;
import com.unitbv.events.dao.impl.DefaultUserDao;

public class EntityDAOImplFactory {

	public static EventDao createNewEventDao(String persistenceUnitName) {
		return new DefaultEventDao(persistenceUnitName);
	}

	public static InvitationDao createNewInvitationDao(String persistenceUnitName) {
		return new DefaultInvitationDao(persistenceUnitName);
	}

	public static InvitationFileDao createNewInvitationFileDao(String persistenceUnitName) {
		return new DefaultInvitationFileDao(persistenceUnitName);
	}

	public static NotificationDao createNewNotificationDao(String persistenceUnitName) {
		return new DefaultNotificationDao(persistenceUnitName);
	}

	public static RoleDao createNewRoleDao(String persistenceUnitName) {
		return new DefaultRoleDao(persistenceUnitName);
	}

	public static UserDao createNewUserDao(String persistenceUnitName) {
		return new DefaultUserDao(persistenceUnitName);
	}

}
