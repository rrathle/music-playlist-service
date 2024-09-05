package com.amazon.ata.music.playlist.service.dependency;


import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DaoModule {
    @Provides
    @Singleton
    public PlaylistDao providePlaylistDao(DynamoDBMapper dynamoDBMapper) {
        return new PlaylistDao(dynamoDBMapper);
    }
    @Provides
    @Singleton
    public AlbumTrackDao provideAlbumTrackDao(DynamoDBMapper dynamoDBMapper) {
        return new AlbumTrackDao(dynamoDBMapper);
    }
    @Provides
    @Singleton
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient(Regions.US_WEST_2));
    }
}
