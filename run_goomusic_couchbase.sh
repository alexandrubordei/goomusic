#!/bin/bash

GOOMUSIC_HOME=/opt/goomusic

OPTS="$OPTS -Dcom.bigstep.GoomusicSongMain.songStoreImpl=com.bigstep.impl.CouchbaseSongStore"
OPTS="$OPTS -Dcom.bigstep.impl.CouchbaseSongStore.bucketName=lastfm"
OPTS="$OPTS -Dcom.bigstep.impl.CouchbaseSongStore.password=lastfm"
OPTS="$OPTS -Dcom.bigstep.impl.CouchbaseSongStore.cbServers=localhost"
echo $OPTS

export JAVA_OPTS=$OPTS

$GOOMUSIC_HOME/build/install/goomusic/bin/goomusic
