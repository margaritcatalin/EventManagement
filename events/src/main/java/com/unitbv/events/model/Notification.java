package com.unitbv.events.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the notification database table.
 * 
 */
@Entity
@NamedQuery(name = "Notification.findAll", query = "SELECT n FROM Notification n")
public class Notification implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int notificationId;

	private String description;

	// bi-directional many-to-many association to User
	@ManyToMany
	@JoinTable(name = "NotificationToUser", joinColumns = @JoinColumn(name = "notificationId"), inverseJoinColumns = @JoinColumn(name = "userId"))
	private List<User> users;

	public Notification() {
	}

	public int getNotificationId() {
		return this.notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}