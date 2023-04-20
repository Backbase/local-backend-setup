LOCATION=$(curl -s https://api.github.com/repos/Backbase/bb-fuel/releases/latest | grep "tag_name" | awk '{print "https://github.com/Backbase/bb-fuel/releases/download/" substr($2, 2, length($2)-3) "/" substr($2, 2, length($2)-3) "-boot.jar"}')
curl -L -o bb-fuel.jar $LOCATION
