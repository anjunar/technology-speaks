import Identity from "./domain/control/Identity";
import User from "./domain/control/User";
import UserInfo from "./domain/control/UserInfo";
import Confirmation from "./domain/security/Confirmation";
import Role from "./domain/control/Role";
import Address from "./domain/control/Address";
import GeoPoint from "./domain/control/GeoPoint";
import Application from "./domain/Application";
import {registerEntity} from "react-ui-simplicity";
import EMail from "./domain/control/EMail";
import WebAuthnLogin from "./domain/security/WebAuthnLogin";
import Credential from "./domain/control/Credential";
import Group from "./domain/control/Group";
import ManagedProperty from "./domain/shared/ManagedProperty";
import Post from "./domain/timeline/Post";
import Document from "./domain/document/Document";
import DocumentSearch from "./domain/document/DocumentSearch";
import Chunk from "./domain/document/Chunk";
import CredentialSearch from "./domain/control/CredentialSearch";
import RoleSearch from "./domain/control/RoleSearch";
import PostSearch from "./domain/timeline/PostSearch";
import UserSearch from "./domain/control/UserSearch";
import GroupSearch from "./domain/control/GroupSearch";
import ChunkSearch from "./domain/document/ChunkSearch";
import RevisionSearch from "./domain/document/RevisionSearch";
import HashTag from "./domain/shared/HashTag";
import Revision from "./domain/document/Revision";

export function init() {
    registerEntity(Application)

    registerEntity(Credential)
    registerEntity(CredentialSearch)
    registerEntity(Identity)
    registerEntity(Role)
    registerEntity(RoleSearch)
    registerEntity(User)
    registerEntity(UserSearch)
    registerEntity(Group)
    registerEntity(GroupSearch)
    registerEntity(UserInfo)
    registerEntity(EMail)
    registerEntity(Address)
    registerEntity(GeoPoint)

    registerEntity(WebAuthnLogin)
    registerEntity(Confirmation)
    registerEntity(ManagedProperty)
    registerEntity(HashTag)

    registerEntity(Post)
    registerEntity(PostSearch)

    registerEntity(Document)
    registerEntity(DocumentSearch)
    registerEntity(Chunk)
    registerEntity(ChunkSearch)
    registerEntity(RevisionSearch)
    registerEntity(Revision)
}

