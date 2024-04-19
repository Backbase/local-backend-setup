# Dropping existing databases
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "DROP DATABASE IF EXISTS accesscontrol_pandp;"
echo "Database accesscontrol_pandp dropped successfully"

mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "DROP DATABASE IF EXISTS arrangement_manager;"
echo "Database arrangement_manager dropped successfully"

mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "DROP DATABASE IF EXISTS backbase_identity;"
echo "Database backbase_identity dropped successfully"

mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "DROP DATABASE IF EXISTS retail_onboarding;"
echo "Database retail_onboarding dropped successfully"

mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "DROP DATABASE IF EXISTS user_manager;" &
echo "Database user_manager dropped successfully"

# Creating new databases
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "CREATE DATABASE accesscontrol_pandp;"
echo "Recreated database accesscontrol_pandp successfully"

mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "CREATE DATABASE arrangement_manager;"
echo "Recreated database arrangement_manager successfully"

mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "CREATE DATABASE backbase_identity;"
echo "Recreated database backbase_identity successfully"

mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "CREATE DATABASE retail_onboarding CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
echo "Recreated database retail_onboarding successfully"

mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "CREATE DATABASE user_manager;"
echo "Recreated database user_manager successfully"

# Array of pod criteria
pod_criteria=("access-control" "backbase-identity-backbaseidentity" "retail-onboarding-backbase-application" "user-manager-usermanage" "arrangement-manager")

# Loop through each pod criteria and delete the first matching pod
for criteria in "${pod_criteria[@]}"; do
    # Get the first pod matching the criteria and delete it
    kubectl get pods | grep "$criteria" | awk 'NR==1 {print $1}' | xargs -I {} kubectl delete pod {} &
    echo "Deleted pod $criteria"
done

# Wait for pod deletions to finish
wait
