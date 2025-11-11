package com.kinotes.kinotes.reminder.service;

import com.kinotes.kinotes.entity.Reminder;

import java.util.List;

public interface ReminderService {

    void addReminder(Reminder reminder);

    void deleteReminder(Reminder reminder);

    void updateReminder(Reminder reminder);

    List<Reminder> getReminders();
}
