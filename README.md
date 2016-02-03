# goomusic
Couchbase demonstrative application which is basically a music search engine.
It is still under active development. 
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
cd goomusic
gradle installDist
```

Install Couchbase
```
rpm -i http://packages.couchbase.com/releases/4.0.0/couchbase-server-community-4.0.0-centos7.x86_64.rpm
#also follow: http://unix.stackexchange.com/questions/245303/failed-to-start-couchbase-server-service-unit-couchbase-server-service-failed-t
```

Get the dataset
```
curl -O http://labrosa.ee.columbia.edu/millionsong/sites/default/files/lastfm/lastfm_train.zip
 /opt/couchbase/bin/cbdocloader -u xxxxxxxx -p xxxxxxxxxx -n localhost -b lastfm lastfm_train.zip
```

