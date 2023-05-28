# mini-aspire
mini-aspire app
It is an app that allows authenticated users to go through a loan application.

**Architecture**


**1) Customer Registration:**
Customer needs to register with mini-aspire before being able to login to the application.
API - http://localhost:8080/api/v1/mini-aspire/login/register
Request JSON - {
    "username" : "abc",
    "password" : "123456789",
    "role" : "ADMIN"
}
Response Header - Authorization : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmMiLCJleHAiOjE2ODUwOTc1MzQsImlhdCI6MTY4NTA5NjkzNH0.wB97Dq9VcRbxlmcHX-4aEu928o-vdVsr__PBGKkdiDzWbfwlA_w1ITOUgDPwPqWXf8wcSs8UXOAHfm4g77J2rw
Response JSON - {
    "registered": true,
    "message": "REGISTRATION SUCCESS"
}

**2) Customer login:**
Customer needs to login to mini-aspire before being able to use the application.
API - http://localhost:8080/api/v1/mini-aspire/login/authenticate
Request JSON - {
    "username" : "abc",
    "password" : "123456789"
}
Response Header - Authorization : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmMiLCJleHAiOjE2ODUwOTc2MDUsImlhdCI6MTY4NTA5NzAwNX0.9ygx0gqFradn0KN3YxMyiwut31WrJ0NVjblh4zHL8Mx1uJ3x1dks688K1SaWffdw8P__ybDPs54vIdkifezPHA
Response JSON - {
    "authenticated": true,
    "message": "AUTHENTICATION SUCCESS"
}

**3) Customer can create a loan:**
Customer applies for a loan request defining amount and term 
example:
  Request amount of 10000 with term 3 on date 7th Feb 2022
  it will generate 3 scheduled repayments:
  14th Feb 2022 with amount 3333.33
  21st Feb 2022 with amount 3333.33
  28th Feb 2022 with amount 3333.34
the loan and scheduled repayments will have state PENDING
API - http://localhost:8080/api/v1/mini-aspire/loan/apply
Request Header - Authorization : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmMiLCJleHAiOjE2ODUwOTc2MDUsImlhdCI6MTY4NTA5NzAwNX0.9ygx0gqFradn0KN3YxMyiwut31WrJ0NVjblh4zHL8Mx1uJ3x1dks688K1SaWffdw8P__ybDPs54vIdkifezPHA
Request JSON - {
    "username" : "abc",
    "loanAmount" : 10000,
    "term" : 8
}
Response JSON - {
    "isApplied": true,
    "loanId": 1,
    "loanDto": {
        "username": "abc",
        "disbersedAmount": 10000.0,
        "repayedAmount": 0.0,
        "emis": [
            {
                "emiAmount": 1250.0,
                "emiDate": "02-06-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1250.0,
                "emiDate": "09-06-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1250.0,
                "emiDate": "16-06-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1250.0,
                "emiDate": "23-06-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1250.0,
                "emiDate": "30-06-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1250.0,
                "emiDate": "07-07-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1250.0,
                "emiDate": "14-07-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1250.0,
                "emiDate": "21-07-2023",
                "isPaid": false
            }
        ],
        "totalTerm": 8,
        "remainingTerm": 8,
        "isActive": false
    },
    "message": "LOAN APPLICATION SUCCESS"
}

**4) Admin approves the loan:**
Admin changes the pending loans to state APPROVED.
API - http://localhost:8080/api/v1/mini-aspire/loan/activate
Request Header - Authorization : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmMiLCJleHAiOjE2ODUwOTc2MDUsImlhdCI6MTY4NTA5NzAwNX0.9ygx0gqFradn0KN3YxMyiwut31WrJ0NVjblh4zHL8Mx1uJ3x1dks688K1SaWffdw8P__ybDPs54vIdkifezPHA
Request JSON - {
    "username" : "abc",
    "loanIds" : [1, 2, 3]
}
Response JSON - {
    "approvalMessages": "1: LOAN APPROVALS SUCCESS,2: INVALID LOAN ID,3: INVALID LOAN ID,"
}

**5) Customer can view the loans belonging to him:**
Policy to make sure that the customers can view their own loans only which have been approved by the Admin.
API - http://localhost:8080/api/v1/mini-aspire/loan/{username}
Request Header - Authorization : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmMiLCJleHAiOjE2ODUwOTc2MDUsImlhdCI6MTY4NTA5NzAwNX0.9ygx0gqFradn0KN3YxMyiwut31WrJ0NVjblh4zHL8Mx1uJ3x1dks688K1SaWffdw8P__ybDPs54vIdkifezPHA
Response JSON - {
    "usename": "abc",
    "loans": [
        {
            "username": "abc",
            "disbersedAmount": 10000.0,
            "repayedAmount": 0.0,
            "emis": [
                {
                    "emiAmount": 1250.0,
                    "emiDate": "02-06-2023",
                    "isPaid": false
                },
                {
                    "emiAmount": 1250.0,
                    "emiDate": "09-06-2023",
                    "isPaid": false
                },
                {
                    "emiAmount": 1250.0,
                    "emiDate": "16-06-2023",
                    "isPaid": false
                },
                {
                    "emiAmount": 1250.0,
                    "emiDate": "23-06-2023",
                    "isPaid": false
                },
                {
                    "emiAmount": 1250.0,
                    "emiDate": "30-06-2023",
                    "isPaid": false
                },
                {
                    "emiAmount": 1250.0,
                    "emiDate": "07-07-2023",
                    "isPaid": false
                },
                {
                    "emiAmount": 1250.0,
                    "emiDate": "14-07-2023",
                    "isPaid": false
                },
                {
                    "emiAmount": 1250.0,
                    "emiDate": "21-07-2023",
                    "isPaid": false
                }
            ],
            "totalTerm": 8,
            "remainingTerm": 8,
            "isActive": true
        }
    ],
    "message": "LOANS FETCH SUCCESS"
}

**6) Customer add a repayment to their loan:**
Customer add a repayment with amount greater or equal to the scheduled repayment. The scheduled repayment changes the status to PAID.
If all the scheduled repayments connected to a loan are PAID, the loan also becomes PAID.
API - http://localhost:8080/api/v1/mini-aspire/loan/repay
Request Header - Authorization : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmMiLCJleHAiOjE2ODUwOTc2MDUsImlhdCI6MTY4NTA5NzAwNX0.9ygx0gqFradn0KN3YxMyiwut31WrJ0NVjblh4zHL8Mx1uJ3x1dks688K1SaWffdw8P__ybDPs54vIdkifezPHA
Request JSON - {
    "username" : "abc",
    "loanId" : 1,
    "repaymentAmount" : 2000
}
Response JSON - {
    "isRepaymentSuccessful": true,
    "loanDto": {
        "username": "abc",
        "disbersedAmount": 10000.0,
        "repayedAmount": 2000.0,
        "emis": [
            {
                "emiAmount": 2000.0,
                "emiDate": "02-06-2023",
                "isPaid": true
            },
            {
                "emiAmount": 1142.857142857143,
                "emiDate": "09-06-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1142.857142857143,
                "emiDate": "16-06-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1142.857142857143,
                "emiDate": "23-06-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1142.857142857143,
                "emiDate": "30-06-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1142.857142857143,
                "emiDate": "07-07-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1142.857142857143,
                "emiDate": "14-07-2023",
                "isPaid": false
            },
            {
                "emiAmount": 1142.8571428571431,
                "emiDate": "21-07-2023",
                "isPaid": false
            }
        ],
        "totalTerm": 8,
        "remainingTerm": 7,
        "isActive": true
    },
    "message": "LOAN REPAYMENT SUCCESS"
}


**Steps to run the application**
1) Clone the repo to local directory
2) Go to the ./mini-aspire-user-loans-app/target directory
3) Run the command "java -jar mini-aspire-user-loans-app-0.0.1-SNAPSHOT.jar" 

**Alternatively, if client wants to build the project themselves**
1) Install maven build tool in the local system
2) Clone the repo to local directory
3) Go to the ./mini-aspire-user-loans-app directory
4) Run the command "mvn clean compile install ."
5) A ./target directory will be created. Go inside the /target directory.
6) Run the command "java -jar mini-aspire-user-loans-app-0.0.1-SNAPSHOT.jar"

**Points for enhancement**
1) Currently password is being sent to auth apis in its raw form. With the inclusion of a UI, password can be encrypted before hitting auth apis.
2) Role based loan access/retrieval: ADMINs are allowed to access all the loans but CUSTOMER roles are only allowed to access their own loans.
3) Maker/Checker implementation for role application and approval: 
    Currently an ADMIN can 
    a) Approve a loan created by someone else
    b) Create a loan and approve the same loan. 
    This behaviour can be enhanced by implementing a maker-checker system, where the person creating the loan request is not allowed to approve the loan request and vice-versa.


