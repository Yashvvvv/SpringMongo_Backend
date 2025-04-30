package com.example.spring_boot_crash_course.controller

import com.example.spring_boot_crash_course.controller.NoteController.NoteResponse
import com.example.spring_boot_crash_course.database.model.Note
import com.example.spring_boot_crash_course.database.repository.NoteRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.awt.Color
import java.time.Instant
import kotlin.coroutines.RestrictsSuspension

//POST https://localhost:8085/notes
//GET https://localhost:8085/notes?ownerId=123
//DELETE https://localhost:8085/notes/123




@RestController
@RequestMapping("/notes")
class NoteController(
    private val repository: NoteRepository,
    private val noteRepository: NoteRepository
) {

    data class NoteRequest(
        val id: String?,
        @field:NotBlank(message = "Title cannot be blank.")
        val title: String,
        val content: String,
        val color: Long
    )

    data class NoteResponse(
        val id: String,
        val title: String,
        val content: String,
        val color: Long,
        val createdAt: Instant,
    )

    @PostMapping
    fun save(
        @Valid @RequestBody body: NoteRequest,
    ): NoteResponse {
        val ownerId = SecurityContextHolder.getContext().authentication.principal.toString()
        val note = repository.save(
            Note(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = body.title,
                content = body.content,
                color = body.color,
                createAt = Instant.now(),
                ownerId = ObjectId(ownerId)
            )
        )
        return note.toResponse()
    }

    @GetMapping
    fun findByOwnerId(): List<NoteResponse> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal.toString()
        return repository.findByOwnerId(ObjectId(ownerId)).map{
            it.toResponse()
        }

    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String){
        val note = noteRepository.findById(ObjectId(id)).orElseThrow(){
            IllegalArgumentException("Note not found.")
        }
        val ownerId = SecurityContextHolder.getContext().authentication.principal.toString()
        if(note.ownerId.toHexString() == ownerId){
            repository.deleteById(ObjectId(id))
        }
    }
}

private fun Note.toResponse(): NoteController.NoteResponse {
    return NoteResponse(
        id = id.toHexString(),
        title = title,
        content = content,
        color = color,
        createdAt = Instant.now(),
    )
}