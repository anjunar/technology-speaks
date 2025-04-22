import {v4} from "uuid";
import MappedSuperclass from "../../mapper/annotations/MappedSuperclass";
import ActiveObject from "./ActiveObject";
import Basic from "../../mapper/annotations/Basic";

@MappedSuperclass("AbstractEntity")
export default class AbstractEntity extends ActiveObject {

    @Basic()
    id : string = v4()

}