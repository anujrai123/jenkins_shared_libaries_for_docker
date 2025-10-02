// vars/deployAnsible.groovy
def call(String playbook, String targetList, String action = "default", String inventory = "inventory/hosts") {
    // Normalize comma-separated target list
    def targets = targetList.split(',').collect { it.trim() }.join(',')

    echo "Running Ansible Playbook: ${playbook}"
    echo "Action: ${action}"
    echo "Targets: ${targets}"

    // Ensure Jenkins SSH key credential is available (mounted in Ansible container)
    withCredentials([sshUserPrivateKey(credentialsId: 'ansible-ssh-key', keyFileVariable: 'SSH_KEY')]) {

        // Run the playbook directly inside the persistent Ansible container
        // Assumes Jenkins container has access to host Docker (via /var/run/docker.sock)
        sh """
        # Check if ansible container exists and is running
        if [ -z "\$(docker ps -q -f name=ansible)" ]; then
            echo "Ansible container not running. Starting it..."
            docker-compose up -d ansible
            sleep 5  # Wait a few seconds for container to be ready
        else
            echo "Ansible container is already running."
        fi

        # Execute the playbook inside the Ansible container
        docker exec ansible \
            ansible-playbook /ansible/playbooks/${playbook} \
            -i /ansible/playbooks/${inventory} \
            --extra-vars "action=${action}" \
            --limit "${targets}"
        """
    }
}
