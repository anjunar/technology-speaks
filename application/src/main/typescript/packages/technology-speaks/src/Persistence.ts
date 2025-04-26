import Identity from "./domain/control/Identity";
import User from "./domain/control/User";
import UserInfo from "./domain/control/UserInfo";
import Confirmation from "./domain/control/Confirmation";
import Role from "./domain/control/Role";
import Address from "./domain/control/Address";
import GeoPoint from "./domain/control/GeoPoint";
import Application from "./domain/Application";
import {registerEntity} from "react-ui-simplicity";
import EMail from "./domain/control/EMail";
import WebAuthnLogin from "./domain/control/WebAuthnLogin";
import Credential from "./domain/control/Credential";
import Group from "./domain/control/Group";
import ManagedProperty from "./domain/core/ManagedProperty";

export function init() {
    registerEntity(Application)
    registerEntity(Confirmation)
    registerEntity(Identity)
    registerEntity(Role)
    registerEntity(User)
    registerEntity(Group)
    registerEntity(UserInfo)
    registerEntity(WebAuthnLogin)
    registerEntity(Credential)
    registerEntity(EMail)
    registerEntity(Address)
    registerEntity(GeoPoint)
    registerEntity(ManagedProperty)
}

