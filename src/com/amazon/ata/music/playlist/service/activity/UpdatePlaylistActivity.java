package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeChangeException;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.UpdatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.UpdatePlaylistResult;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * Implementation of the UpdatePlaylistActivity for the MusicPlaylistService's UpdatePlaylist API.
 *
 * This API allows the customer to update their saved playlist's information.
 */
public class UpdatePlaylistActivity implements RequestHandler<UpdatePlaylistRequest, UpdatePlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;

    /**
     * Instantiates a new UpdatePlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     */
    @Inject
    public UpdatePlaylistActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    /**
     * This method handles the incoming request by retrieving the playlist, updating it,
     * and persisting the playlist.
     * <p>
     * It then returns the updated playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the provided playlist name or customer ID has invalid characters, throws an
     * InvalidAttributeValueException
     * <p>
     * If the request tries to update the customer ID,
     * this should throw an InvalidAttributeChangeException
     *
     * @param updatePlaylistRequest request object containing the playlist ID, playlist name, and customer ID
     *                              associated with it
     * @return updatePlaylistResult result object containing the API defined {@link PlaylistModel}
     */
    @Override
    public UpdatePlaylistResult handleRequest(final UpdatePlaylistRequest updatePlaylistRequest, Context context) {
        log.info("Received UpdatePlaylistRequest {}", updatePlaylistRequest);

        Playlist playlist = playlistDao.getPlaylist(updatePlaylistRequest.getId());
        //uses the playlistDao to get playlist, uses the provided updateplauylist request to retrieve the id

        if (playlist == null) {
            // Playlist not found, throw an exception
            throw new InvalidAttributeValueException("Playlist not found for ID: " + updatePlaylistRequest.getId());
        }

        // Step 2: Validate the customer ID
        if (!playlist.getCustomerId().equals(updatePlaylistRequest.getCustomerId())) {
            // Compares the "customerId from the request with the customerId stored in the plaulist
            throw new InvalidAttributeChangeException("Customer ID cannot be changed.");
        }

        // Step 3: Validate the playlist name
        if (!MusicPlaylistServiceUtils.isValidString(updatePlaylistRequest.getName())) {
            // Invalid playlist name, throw an exception if it contains invalid chacters
            throw new InvalidAttributeValueException("Invalid characters in playlist name: " + updatePlaylistRequest.getName());
        }

        // Step 4: Update the playlist name (other attributes remain unchanged)
        playlist.setName(updatePlaylistRequest.getName());
        // updates the name ti the vlaue provided i the request that is the parameter that is passed to the method

        // Step 5: Persist the updated playlist, saves  the updated playlist back to the database, it uses PlayListDao class toi handle the database operation
        playlistDao.savePlaylist(playlist);

        // Step 6: Construct and return the response
        PlaylistModel updatedPlaylistModel = new ModelConverter().toPlaylistModel(playlist);
        return UpdatePlaylistResult.builder()
                .withPlaylist(updatedPlaylistModel)
                .build();
    }
}
