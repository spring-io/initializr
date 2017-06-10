#!/bin/sh

set -e
# set -x

VERSION="1.4.7.RELEASE"
if [ -z "${PREFIX}" ]; then
    PREFIX="/usr/local/bin"
fi
if [ -z "${JAR_FILE}" ]; then
    JAR_FILE="/tmp/spring.zip"
    if [ -z "${JAR_URL}" ]; then

        case "${VERSION}" in
            *SNAPSHOT) REPO=snapshot ;;
            *.M*) REPO=milestone ;;
            *.RC*) REPO=milestone ;;
            *) REPO=release ;;
        esac

        echo "Downloading spring ${VERSION} distribution from repo.spring.io/${REPO}"
        echo
        
	    JAR_URL="https://repo.spring.io/${REPO}/org/springframework/boot/spring-boot-cli/${VERSION}/spring-boot-cli-${VERSION}-bin.zip"
        curl --progress-bar --fail "$JAR_URL" -o "$JAR_FILE"

    fi
fi
trap "echo Installation failed." EXIT

test -f "${JAR_FILE}"

if [ -z "${SPRING_HOME}" ]; then
    SPRING_HOME="${HOME}/.spring"
fi

mkdir -p "${SPRING_HOME}"
cd "${SPRING_HOME}"
rm -rf spring* bin lib
unzip -o "$JAR_FILE"
mv spring*/* .
rm -rf spring*
echo

test -x "${SPRING_HOME}/bin/spring"

echo "spring ${VERSION} has been installed in your home directory (~/.spring)."
echo

if rm -f "$PREFIX/spring" && ln -sf "${SPRING_HOME}/bin/spring" "$PREFIX/spring" >/dev/null 2>&1; then
    echo
    echo "Linking ${SPRING_HOME}/bin/spring to $PREFIX/spring for your convenience."
      cat <<"EOF"

To get started:

  $ spring --version
  $ spring help

And take a look at the README at https://github.com/spring-projects/spring-boot#readme.

EOF
elif type sudo >/dev/null 2>&1; then
    echo "Linking ${SPRING_HOME}/bin/spring to $PREFIX/spring for your convenience."
    echo "This may prompt for your password."
    if sudo rm -f "$PREFIX/spring" && sudo ln -sf "${SPRING_HOME}/bin/spring" "$PREFIX/spring" >/dev/null 2>&1; then
      cat <<"EOF"

To get started:

  $ spring --version
  $ spring help

And take a look at the README at https://github.com/spring-projects/spring-boot#readme.

EOF
    else
        cat <<"EOF"
Couldn't create the symlink. Please either:
  (1) Run the following as root:
        cp ${SPRING_HOME}/bin/spring /usr/bin/spring
  (2) Add ${SPRING_HOME}/bin to your path, or
  (3) Rerun this command to try again.

Then to get started, take a look at 'spring help' or see the README at
https://github.com/spring-projects/spring-boot#readme.
EOF
    fi
else
  cat <<"EOF"

Now you need to do one of the following:

  (1) Add ~/.spring to your path, or
  (2) Run this command as root:
        cp ${SPRING_HOME}/bin /usr/bin/spring

Then to get started, take a look at 'spring help' or see the README at
https://github.com/spring-projects/spring-boot#readme.
EOF
fi

trap - EXIT
