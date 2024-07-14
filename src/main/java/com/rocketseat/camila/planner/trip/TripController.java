package com.rocketseat.camila.planner.trip;

import com.rocketseat.camila.planner.activity.ActivitiesDTO;
import com.rocketseat.camila.planner.activity.ActivityRequestPayload;
import com.rocketseat.camila.planner.activity.ActivityResponse;
import com.rocketseat.camila.planner.activity.ActivityService;
import com.rocketseat.camila.planner.link.LinkRequestPayload;
import com.rocketseat.camila.planner.link.LinkResponse;
import com.rocketseat.camila.planner.link.LinkService;
import com.rocketseat.camila.planner.link.LinksDTO;
import com.rocketseat.camila.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {
    @Autowired
    private ParticipantService participantService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private TripRepository repository;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload){
        Trip newTrip = new Trip(payload);

        this.repository.save(newTrip);

        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rowTrip = trip.get();

            rowTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            rowTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            rowTrip.setDestination(payload.destination());

            this.repository.save(rowTrip);

            return ResponseEntity.ok(rowTrip);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rowTrip = trip.get();
            rowTrip.setIsConfirmed(true);
            this.repository.save(rowTrip);

            this.participantService.triggerConfirmationEmailToParticipants(id);
            return ResponseEntity.ok(rowTrip);
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantsRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rowTrip = trip.get();
            ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToEvent( payload.email(), rowTrip);

            if(rowTrip.getIsConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

            return ResponseEntity.ok(participantResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDTO>> getAllParticipants(@PathVariable UUID id){
        List<ParticipantDTO> participantList = this.participantService.getAllParticipantsFromEvent(id);

        return ResponseEntity.ok(participantList);
    }
    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rowTrip = trip.get();
            ActivityResponse activityResponse = this.activityService.registerActivity(payload, rowTrip);

            return ResponseEntity.ok(activityResponse);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivitiesDTO>> getAllActivities(@PathVariable UUID id){
        List<ActivitiesDTO> activitiesDTOList = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activitiesDTOList);
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLinks(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rowTrip = trip.get();
            LinkResponse linkResponse = this.linkService.registerLink(payload, rowTrip);

            return ResponseEntity.ok(linkResponse);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinksDTO>> getAllLinks(@PathVariable UUID id){
        List<LinksDTO> linksDTOList = this.linkService.getAllLinksFromId(id);

        return ResponseEntity.ok(linksDTOList);
    }
}

