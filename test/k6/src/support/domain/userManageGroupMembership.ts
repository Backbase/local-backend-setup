class UserManageGroupMembership {
    manageGroupMembership: boolean;
    view: boolean;
    mapRoles: boolean;
    impersonate: boolean;
    manage: boolean;

    constructor(
        manageGroupMembership: boolean,
        view: boolean,
        mapRoles: boolean,
        impersonate: boolean,
        manage: boolean) {
        this.manageGroupMembership = manageGroupMembership;
        this.view = view;
        this.mapRoles = mapRoles;
        this.impersonate = impersonate;
        this.manage = manage;
    }
}