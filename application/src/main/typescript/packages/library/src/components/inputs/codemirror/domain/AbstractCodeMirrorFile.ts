import {Basic} from "../../../../mapper";
import {AbstractEntity} from "../../../../domain/container";

export abstract class AbstractCodeMirrorFile extends AbstractEntity {
    
    @Basic()
    name : string
    
}