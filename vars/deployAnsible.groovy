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
docker exec ansible \
  ansible-playbook /ansible/playbooks/install_ansible.yml \
  -i /ansible/playbooks/inventory/hosts \
  --extra-vars "action=${params.ACTION}" \
  --limit "${params.SERVER_LIST}"
"""

    }
}
