@Library("Shared_new") _

pipeline {
    agent any

    parameters {
        choice(name: 'ACTION', choices: ['Pre-Check', 'Install', 'Uninstall'], description: 'Select action to perform')
        text(name: 'TARGET_LIST', defaultValue: 'target-container-1,target-container-2', description: 'Comma-separated list of target containers')
    }

    stages {
        stage('Print Inputs') {
            steps {
                script {
                    echo "Selected Action: ${params.ACTION}"
                    echo "Target Containers: ${params.TARGET_LIST}"
                }
            }
        }

        stage('Run Ansible Playbook') {
            steps {
                script {
                    deployAnsible("playbook.yml", params.TARGET_LIST, params.ACTION)
                }
            }
        }

        stage('Post Actions') {
            steps {
                script {
                    echo "Pipeline finished successfully for targets: ${params.TARGET_LIST} with action ${params.ACTION}"
                }
            }
        }
    }
}
