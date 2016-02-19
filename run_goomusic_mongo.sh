#!/bin/bash

GOOMUSIC_HOME=/opt/goomusic


OPTS="$OPTS -Dcom.bigstep.impl.MongoSongStore.databaseName=lastfm"
OPTS="$OPTS -Dcom.bigstep.impl.MongoSongStore.collectionName=songs" 
OPTS="$OPTS -Dcom.bigstep.GoomusicSongMain.songStoreImpl=com.bigstep.impl.MongoSongStore"
echo $OPTS

export JAVA_OPTS=$OPTS

$GOOMUSIC_HOME/build/install/goomusic/bin/goomusic
