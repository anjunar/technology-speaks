import AbstractEntity from "../core/AbstractEntity";
import {Basic, Entity} from "react-ui-simplicity";
import User from "./User";

@Entity("Group")
export default class Group extends AbstractEntity {

    $type = "Group"

    @Basic()
    name : string

    @Basic()
    description : string

    @Basic()
    users : User[]


}