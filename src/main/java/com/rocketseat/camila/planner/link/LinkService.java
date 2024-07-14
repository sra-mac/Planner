package com.rocketseat.camila.planner.link;

import com.rocketseat.camila.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {
    @Autowired
    private LinkRepository repository;

    public LinkResponse registerLink(LinkRequestPayload payload, Trip trip){
        Link link = new Link(payload.title(), payload.url(), trip);

        this.repository.save(link);

        return new LinkResponse(link.getId());
    }

    public List<LinksDTO> getAllLinksFromId(UUID tripId){
        return this.repository.findByTripId(tripId).stream()
                .map(link -> new LinksDTO(link.getId(), link.getTitle(), link.getUrl())).toList();
    }
}
