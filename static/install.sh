#!/bin/sh

set -e
# set -x

VERSION="0.5.0.M1"
if [ -z "${PREFIX}" ]; then
    PREFIX="/usr/local/bin"
fi
if [ -z "${JAR_FILE}" ]; then
    JAR_FILE="/tmp/spring.jar"
    if [ -z "${JAR_URL}" ]; then

        echo "Downloading spring ${VERSION} distribution"
        echo

	    JAR_URL="https://repo.springsource.org/milestone/org/springframework/boot/spring-boot-cli/${VERSION}/spring-boot-cli-${VERSION}.jar"
        curl --progress-bar --fail "$JAR_URL" -o "$JAR_FILE"

    fi
fi
trap "echo Installation failed." EXIT

test -f "${JAR_FILE}"

SPRING_HOME="${HOME}/.spring"
mkdir -p "$SPRING_HOME"
cp "$JAR_FILE" "${SPRING_HOME}/spring.jar"
cd "${SPRING_HOME}"
echo

rm -rf "${SPRING_HOME}"/spring
cat > "${SPRING_HOME}"/spring <<"EOF"
#!/bin/sh
java -jar ${SPRING_HOME}/spring.jar \$*
EOF
chmod +x "${SPRING_HOME}/spring"
test -x "${SPRING_HOME}/spring"

echo "spring ${VERSION} has been installed in your home directory (~/.spring)."
echo

if rm -f "$PREFIX/spring" && ln -sf "${SPRING_HOME}/spring" "$PREFIX/spring" >/dev/null 2>&1; then
    echo
    echo "Linking ~/.spring/spring to $PREFIX/spring for your convenience."
      cat <<"EOF"

To get started:

  $ spring --version
  $ spring help

And take a look at the README at https://github.com/springsource/spring-boot#readme.

EOF
elif type sudo >/dev/null 2>&1; then
    echo "Linking ~/.spring/spring to $PREFIX/spring for your convenience."
    echo "This may prompt for your password."
    if sudo rm -f "$PREFIX/spring" && sudo ln -sf "${SPRING_HOME}/spring" "$PREFIX/spring" >/dev/null 2>&1; then
      cat <<"EOF"

To get started:

  $ spring --version
  $ spring help

And take a look at the README at https://github.com/springsource/spring-boot#readme.

EOF
    else
        cat <<"EOF"
Couldn't create the symlink. Please either:
  (1) Run the following as root:
        cp ~/.spring/spring /usr/bin/spring
  (2) Add ~/.spring to your path, or
  (3) Rerun this command to try again.

Then to get started, take a look at 'spring help' or see the README at
https://github.com/springsource/spring-boot#readme.
EOF
    fi
else
  cat <<"EOF"

Now you need to do one of the following:

  (1) Add ~/.spring to your path, or
  (2) Run this command as root:
        cp ~/.spring/spring /usr/bin/spring

Then to get started, take a look at 'spring help' or see the README at
https://github.com/springsource/spring-boot#readme.
EOF
fi

trap - EXIT