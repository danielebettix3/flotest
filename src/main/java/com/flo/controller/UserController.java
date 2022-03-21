package com.flo.controller;

import com.flo.entity.User;
import com.flo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController
{
    final UserRepository repository;

    /*********************************
     *        GET                     *
     **********************************/
    @Operation(summary = "Get a user by its id",
            security = { @SecurityRequirement(name = "basicAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the User",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<User> getUser(
            @PathVariable long id)
    {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(user);
    }

    @Operation(summary = "Get the users by theirs name and surname",
            security = { @SecurityRequirement(name = "basicAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Users",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<User>> findUsers(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome)
    {
        try
        {
            if (nome != null && cognome != null)
            {
                return ResponseEntity
                        .status(HttpStatus.OK.value())
                        .body( repository.findByNomeIgnoreCaseAndAndCognomeIgnoreCase(nome, cognome) );

            }
            if (nome != null)
            {
                return ResponseEntity
                        .status(HttpStatus.OK.value())
                        .body(repository.findByNomeIgnoreCase(nome));

            }
            if (cognome != null)
            {
                return ResponseEntity
                        .status(HttpStatus.OK.value())
                        .body(repository.findByCognomeIgnoreCase(cognome));

            }

            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(StreamSupport
                            .stream(repository.findAll().spliterator(), false)
                            .collect(Collectors.toList()));
        } catch (Exception e)
        {
            log.error(e.getMessage(),e);
            return ResponseEntity.internalServerError().build();
        }

    }

    /*********************************
     *        PUT                     *
     **********************************/
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update user",
            security = { @SecurityRequirement(name = "basicAuth") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the User",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)
    })

    public ResponseEntity<User> updateUser(
            @PathVariable("id") final Long id,
            @Valid @RequestBody final User user)
    {

        if(id.equals(user.getId()))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).build();
        }
        User save = repository.save(user);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED.value())
                .body(save);
    }

    /*********************************
     *        POST                     *
     **********************************/
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User saved",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid User",
                    content = @Content)
    })
    public ResponseEntity<User> createUser(
            @Valid @RequestBody final User user)
    {

        user.setId(null);

        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body( repository.save(user) );
    }


    /*********************************
     *        DELETE                   *
     **********************************/
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid User",
                    content = @Content)
    })
    public ResponseEntity<User> deleteUser(
            @PathVariable("id") final Long id,
            @Valid @RequestBody final User user)
    {
        if(id.equals(user.getId()))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).build();
        }
        repository.delete(user);

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .build();
    }

}
