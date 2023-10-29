pipeline
{
    agent any
    
    tools{
        
        maven 'maven'
        
    }
    stages
    {
    
    stage('Build')
    {
    
    steps
    {
    
    git 'https://github.com/jglick/simple-maven-project-with-tests.git'
    bat "mvn -Dmaven.test.failure.ignore=true clean package"
}
post
{
    
    success
    {
    
    junit '**/target/surefire-reports/TEST-*.xml'
    archiveArtifacts 'target/*.jar'
}
}
}
stage ("Deploy to QA")
{
    
    steps{
        
        echo("deploy to qa")
    }

}
 stage('Regression Automation Test')
 {
    
    steps
    {
    
    catchError(buildResult: 'SUCCESS', stageResult:'FAILURE'){
                                
                                git 'https://github.com/php7134/2023POMSeries.git'
                                bat "mvn clean test -Dsurefire.suiteXmlFiles=src/main/resources/testrunners/testng_regression.xml"
                            }

}
}

stage ('Publish Allure Reports')
{
    steps{
        script{
        allure([ 
        includeProperties: false,
        jdk:'',
        properties:[],
        reportBuildPolicy: 'Always',
        results:[[ path:'/allure-results' ]]
         ])
            
        }
}
}
stage('Publish Extent Report')
{
    
    steps
    { publishHTML([allowMissing:false,
                  alwaysLinkToLastBuild: false,
                  keepAll: true,
                  reportDir:'reports',
                  reportFiles:'TestExecutionReport.html',
                  reportName:'HTML Regression Extent Report',
                  reportTitle:''])
                  
                  }        
}
stage("Deploy to stage"){
    
    steps{
     echo("deploy to stage")
   }
}
stage('Sanity Automation Test'){
    steps{
     catchError(buildResult: 'SUCCESS', stageResult:'FAILURE'){
                                git 'https://github.com/php7134/2023POMSeries.git'
                                bat "mvn clean test -Dsurefire.suiteXmlFiles=src/main/resources/testrunners/testng_sanity.xml"
}
}
}
stage('publish sanity Extent Report')
{
    
    steps{
    catchError(buildResult: 'SUCCESS', stageResult:'FAILURE'){
                                git 'https://github.com/php7134/2023POMSeries.git'
                                bat "mvn clean test -Dsurefire.suiteXmlFiles=src/main/resources/testrunners/testng_sanity.xml"
        
        }
    }

}
stage('Publish Sanity Extent Report')
{
    
    steps
    { publishHTML([allowMissing:false,
                  alwaysLinkToLastBuild: false,
                  keepAll: true,
                  reportDir:'reports',
                  reportFiles:'TestExecutionReport.html',
                  reportName:'HTML Sanity Extent Report',
                  reportTitle:''])
                  
                  }        
}
}
}