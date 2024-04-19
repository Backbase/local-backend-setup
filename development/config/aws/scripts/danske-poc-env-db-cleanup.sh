# Dropping existing databases
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "DROP DATABASE IF EXISTS accesscontrol_pandp;" &
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "DROP DATABASE IF EXISTS arrangement_manager;" &
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "DROP DATABASE IF EXISTS backbase_identity;" &
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "DROP DATABASE IF EXISTS retail_onboarding;" &
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "DROP DATABASE IF EXISTS user_manager;" &

# Wait for database deletion operations to finish
wait

# Creating new databases
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "CREATE DATABASE accesscontrol_pandp;" &
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "CREATE DATABASE arrangement_manager;" &
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "CREATE DATABASE backbase_identity;" &
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "CREATE DATABASE retail_onboarding CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" &
mysql -h danske-poc-db-instance-1.cjgq66g4s8nw.us-east-1.rds.amazonaws.com -u admin -p -e "CREATE DATABASE user_manager;" &

# Wait for database creation operations to finish
wait

# Array of pod criteria
pod_criteria=("access-control" "backbase-identity-backbaseidentity" "retail-onboarding-backbase-application" "user-manager-usermanage" "arrangement-manager")

# Loop through each pod criteria and delete the first matching pod
for criteria in "${pod_criteria[@]}"; do
    # Get the first pod matching the criteria and delete it
    kubectl get pods | grep "$criteria" | awk 'NR==1 {print $1}' | xargs -I {} kubectl delete pod {} &
done

# Wait for pod deletions to finish
wait
