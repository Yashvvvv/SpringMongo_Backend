package com.example.spring_boot_crash_course.database.repository

import com.example.spring_boot_crash_course.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository: MongoRepository<Note, ObjectId> {
    fun findByOwnerId(ownerId: ObjectId): List<Note> //List of all notes by that owner
}