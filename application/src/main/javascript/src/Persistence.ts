import AbstractEntity from "./domain/core/AbstractEntity";
import Identity from "./domain/user/Identity";
import User from "./domain/user/User";
import UserInfo from "./domain/user/UserInfo";
import Confirmation from "./domain/user/Confirmation";
import Role from "./domain/user/Role";
import Address from "./domain/user/Address";
import GeoPoint from "./domain/user/GeoPoint";
import Application from "./domain/Application";
import {registerEntity} from "react-ui-simplicity";
import EMail from "./domain/user/EMail";
import WebAuthnLogin from "./domain/user/WebAuthnLogin";
import Credential from "./domain/user/Credential";

export function init() {
    registerEntity(Application)
    registerEntity(AbstractEntity)
    registerEntity(Confirmation)
    registerEntity(Identity)
    registerEntity(Role)
    registerEntity(User)
    registerEntity(UserInfo)
    registerEntity(WebAuthnLogin)
    registerEntity(Credential)
    registerEntity(EMail)
    registerEntity(Address)
    registerEntity(GeoPoint)
}

