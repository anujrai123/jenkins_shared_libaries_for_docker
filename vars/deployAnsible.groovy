def call(String playbook, String targetList, String action = "default", String inventory = "inventory/hosts") {
    def targets = targetList.split(',').collect { it.trim() }.join(',')

    echo "Running Ansible Playbook: ${playbook}"
    echo "Action: ${action}"
    echo "Targets: ${targets}"

    withCredentials([sshUserPrivateKey(credentialsId: 'ansible-ssh-key', keyFileVariable: 'SSH_KEY')]) {
        // Directly run playbook inside the persistent container (assumes it's already running)
        sh """
        docker exec ansible \
            ansible-playbook /ansible/playbooks/${playbook} \
            -i /ansible/playbooks/${inventory} \
            --extra-vars "action=${action}" \
            --limit "${targets}"
        """
    }
}
