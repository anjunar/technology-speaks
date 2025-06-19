import {createSystem, createVirtualTypeScriptEnvironment} from "@typescript/vfs";
import CodeMirror from "../CodeMirror";

export class FileService {
    constructor(private restApi : CodeMirror.Configuration,
                private system: ReturnType<typeof createSystem>,
                private env: ReturnType<typeof createVirtualTypeScriptEnvironment>) {}

    async createFile(file : CodeMirror.FileEntry) {
        this.env.createFile(file.name, file.content);

        let res = await this.restApi.updateFile(file);

        if (!res.ok) throw new Error("Speichern fehlgeschlagen");
    }

    async updateFile(file : CodeMirror.FileEntry) {
        this.env.updateFile(file.name, file.content);

        let res = await this.restApi.updateFile(file);

        if (!res.ok) throw new Error("Speichern fehlgeschlagen");
    }

    async deleteFile(path: string) {
        this.env.deleteFile(path);

        let res = await this.restApi.deleteFile(path.substring(1));

        if (!res.ok) throw new Error("Löschen fehlgeschlagen");
    }

    async renameFile(oldPath: string, newPath: string) {
        const content = this.system.readFile(oldPath);
        if (!content) throw new Error("Datei nicht gefunden");

        this.env.deleteFile(oldPath);

        this.env.updateFile(newPath, content);

        let res = await this.restApi.renameFile(oldPath.substring(1), newPath);

        if (!res.ok) throw new Error("Umbenennen fehlgeschlagen");
    }

}
