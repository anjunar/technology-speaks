import {createSystem, createVirtualTypeScriptEnvironment} from "@typescript/vfs";
import CodeMirror from "../CodeMirror";
import {AbstractCodeMirrorFile} from "../domain/AbstractCodeMirrorFile";
import {CodeMirrorHTML} from "../domain/CodeMirrorHTML";
import {CodeMirrorTS} from "../domain/CodeMirrorTS";
import {CodeMirrorCSS} from "../domain/CodeMirrorCSS";
import {CodeMirrorImage} from "../domain/CodeMirrorImage";

export class FileService {
    constructor(private restApi : CodeMirror.Configuration,
                private system: ReturnType<typeof createSystem>,
                private env: ReturnType<typeof createVirtualTypeScriptEnvironment>) {}

    async bulk(files : AbstractCodeMirrorFile[]) {
        return await this.restApi.bulk(files)
    }

    async createFile(file : AbstractCodeMirrorFile) {
        if (file instanceof CodeMirrorTS) {
            this.env.createFile(file.name, file.content);
        } else {
            if (file instanceof CodeMirrorHTML || file instanceof CodeMirrorCSS) {
                this.system.writeFile(file.name, file.content);
            } else {
                if (file instanceof CodeMirrorImage) {
                    this.system.writeFile(file.name, file.data);
                }
            }
        }

        let res = await this.restApi.updateFile(file);

        if (!res.ok) throw new Error("Speichern fehlgeschlagen");
    }

    async updateFile(file : AbstractCodeMirrorFile) {
        if (file instanceof CodeMirrorTS) {
            this.env.updateFile(file.name, file.content);
        } else {
            if (file instanceof CodeMirrorHTML || file instanceof CodeMirrorCSS) {
                this.system.writeFile(file.name, file.content);
            } else {
                if (file instanceof CodeMirrorImage) {
                    this.system.writeFile(file.name, file.data);
                }
            }
        }

        let res = await this.restApi.updateFile(file);

        if (!res.ok) throw new Error("Speichern fehlgeschlagen");
    }

    async deleteFile(path: string) {
        this.system.deleteFile(path);

        let res = await this.restApi.deleteFile(path.substring(1));

        if (!res.ok) throw new Error("Löschen fehlgeschlagen");
    }

    async renameFile(oldPath: string, newPath: string) {
        const content = this.system.readFile(oldPath);
        if (!content) throw new Error("Datei nicht gefunden");

        this.system.deleteFile(oldPath);

        this.system.writeFile(newPath, content);

        let res = await this.restApi.renameFile(oldPath.substring(1), newPath);

        if (!res.ok) throw new Error("Umbenennen fehlgeschlagen");
    }

}
