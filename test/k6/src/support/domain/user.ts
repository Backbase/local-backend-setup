class User {
    id: string;
    createdTimestamp: number;
    username: string;
    enabled: boolean;
    totp: boolean;
    emailVerified: boolean;
    firstName: string;
    lastName: string;
    email: string;
    attributes: UserAttributes;
    disableableCredentialTypes: Object[];
    requiredActions: Object[];
    notBefore: number;
    access: UserManageGroupMembership;

    constructor(
        id: string,
        createdTimestamp: number,
        username: string,
        enabled: boolean,
        totp: boolean,
        emailVerified: boolean,
        firstName: string,
        lastName: string,
        email: string,
        attributes: UserAttributes,
        disableableCredentialTypes: Object[],
        requiredActions: Object[],
        notBefore: number,
        access: UserManageGroupMembership
    ) {
        this.id = id;
        this.createdTimestamp = createdTimestamp;
        this.username = username;
        this.enabled = enabled;
        this.totp = totp;
        this.emailVerified = emailVerified;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.attributes = attributes;
        this.disableableCredentialTypes = disableableCredentialTypes;
        this.requiredActions = requiredActions;
        this.notBefore = notBefore;
        this.access = access;
    }
}