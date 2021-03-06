# goomusic

Vert.x microservice offering a music search service on top of the lastfm dataset. It can use both mongo and couchbase backends. It is fully asyncronous and uses websockets.

```
yum install epel-release
yum instal git maven java-1.8.0-openjdk-devel 
gradle_version=2.9
curl -O https://downloads.gradle.org/distributions/gradle-${gradle_version}-all.zip
sudo unzip gradle-${gradle_version}-all.zip 
mv gradle-${gradle_version} /opt/gradle
sudo ln -sfn gradle-${gradle_version} /opt/gradle/latest
sudo printf "export GRADLE_HOME=/opt/gradle/latest\nexport PATH=\$PATH:\$GRADLE_HOME/bin" > /etc/profile.d/gradle.sh
. /etc/profile.d/gradle.sh
# check installation
gradle -v
rm gradle-2.9-all.zip
```
Build and install the app
```
cd /opt/
git clone 
cd goomusic
gradle installDist
```

Install Couchbase
```
rpm -i http://packages.couchbase.com/releases/4.0.0/couchbase-server-community-4.0.0-centos7.x86_64.rpm
#also follow: http://unix.stackexchange.com/questions/245303/failed-to-start-couchbase-server-service-unit-couchbase-server-service-failed-t
```

Get and load the dataset
```
curl -O http://labrosa.ee.columbia.edu/millionsong/sites/default/files/lastfm/lastfm_train.zip
 /opt/couchbase/bin/cbdocloader -u xxxxxxxx -p xxxxxxxxxx -n localhost -b lastfm lastfm_train.zip
```

Create song/artist view (index)
```
function (doc, meta) {
  emit(doc.artist.toLowerCase().replace(/ /g,''), doc);
}
```


Run the app
```
/opt/goomusic/build/install/goomusic/bin/goomusic -Dcom.bigstep.GoomusicSongMain.songStoreImpl=com.bigstep.impl.CouchbaseSongStore -Dcom.bigstep.impl.CouchbaseSongStore.bucketName=lastfm -Dcom.bigstep.impl.CouchbaseSongStore.password=lastfm -Dcom.bigstep.impl.CouchbaseSongStore.cbServers=localhost
```

It also supports mongo but you need to create another lowercase field within the dataset and also create indexes:
```
db.songs.find({}).forEach(function(doc){ db.songs.update( {_id:doc._id},
			{ $set: {"artist_lc":doc.artist.toLowerCase().replace(" ","") } } ) })
db.songs.remove({$where: "this.artist_lc.length > 40"})
db.songs.createIndex({artist_lc:1})
db.songs.createIndex({track_id:1})
```
