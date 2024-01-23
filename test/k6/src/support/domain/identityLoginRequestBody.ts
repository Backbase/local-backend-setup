class IdentityLoginRequestBody {
    username: string;
    password: string;
    grant_type: string;
    client_id: string;
   
    constructor(client_id: string) { 
        this.client_id = client_id;

        this.username = 'admin';
        this.password = 'admin';
        this.grant_type= 'password';
    }
}

export = IdentityLoginRequestBody;
