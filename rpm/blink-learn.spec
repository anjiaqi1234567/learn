##############################################################
# http://twiki.corp.alimama.com/twiki/bin/view/Alimm_OPS/RPM #
# http://www.rpm.org/max-rpm/ch-rpm-inside.html              #
##############################################################
# Avoid jar repack (brp-java-repack-jars)
%define __jar_repack 0

# Avoid CentOS 5/6 extras processes on contents (especially brp-java-repack-jars)
%define __os_install_post %{nil}

Name: blink-learn
Version:1.0.6
Release: %(echo $RELEASE)%{?dist}
Summary: blink-learn
Group: plt
License: commercial
BuildRoot: %{BUILD}/%{name}-%{version}-%{release}
BuildArch: noarch
AutoReq: no


%description
the topology to produce the realtime frequency

%prep

%build
cd $OLDPWD/../
mvn -U clean package -Dmaven.test.skip=true

%install
DB_ROOT=.%{_prefix}/share/dam/apps/blink-learn

mkdir -p $DB_ROOT/conf
mkdir -p $DB_ROOT/lib
mkdir -p $DB_ROOT/logs
mkdir -p $DB_ROOT/bin

cp $OLDPWD/../conf/* $DB_ROOT/conf
cp $OLDPWD/../bin/* $DB_ROOT/bin
cp $OLDPWD/../metrics/target/*.jar $DB_ROOT/lib

%pre

%post
chmod +x $DB_ROOT/bin/*

%postun

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,ads,users)
%{_prefix}

%changelog
