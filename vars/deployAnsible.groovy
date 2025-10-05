def call(String playbook, String targetList, String action = "default", String inventory = "inventory/hosts") {
    // Split the comma-separated targets and trim spaces
    def targets = targetList.split(',').collect { it.trim() }.join(',')

    echo "Running Ansible Playbook: ${playbook}"
    echo "Action: ${action}"
    echo "Targets: ${targets}"

    // Use credentials for SSH key
    withCredentials([sshUserPrivateKey(credentialsId: 'automation', keyFileVariable: 'SSH_KEY')]) {
        // Run Ansible playbook inside the ansible container using automation user
        sh """
        docker exec ansible \
            ansible-playbook /ansible/playbooks/${playbook} \
            -i /ansible/playbooks/${inventory} \
            --extra-vars "action=${action}" \
            --limit "${targets}" \
            --private-key ${SSH_KEY}
        """
    }
}
