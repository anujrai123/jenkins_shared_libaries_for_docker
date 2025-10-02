// vars/deployAnsible.groovy
def call(String playbook, String targetList, String action = "default", String inventory = "inventory/hosts") {
    // Split comma-separated target list
    def targets = targetList.split(',').collect { it.trim() }.join(',')

    echo "Running Ansible Playbook: ${playbook}"
    echo "Action: ${action}"
    echo "Targets: ${targets}"

    // Ensure SSH key is mounted from Jenkins credentials
    withCredentials([sshUserPrivateKey(credentialsId: 'ansible-ssh-key', keyFileVariable: 'SSH_KEY')]) {

        // Run ephemeral Ansible container
        sh """
            docker run --rm -i \
                -v \$PWD/playbooks:/ansible/playbooks \
                -v \$SSH_KEY:/root/.ssh/id_rsa:ro \
                cytopia/ansible:latest \
                ansible-playbook /ansible/playbooks/${playbook} \
                -i /ansible/playbooks/${inventory} \
                --extra-vars "action=${action}" \
                --limit "${targets}"
        """
    }
}
