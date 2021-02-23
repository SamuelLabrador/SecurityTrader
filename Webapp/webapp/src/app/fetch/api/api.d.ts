/**
 * Security Trader API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0-SNAPSHOT
 *
 *
 * NOTE: This file is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the file manually.
 */
/// <reference path="../custom.d.ts" />
import { Configuration } from "./configuration";
/**
 *
 * @export
 */
export declare const COLLECTION_FORMATS: {
    csv: string;
    ssv: string;
    tsv: string;
    pipes: string;
};
/**
 *
 * @export
 * @interface FetchAPI
 */
export interface FetchAPI {
    (url: string, init?: any): Promise<Response>;
}
/**
 *
 * @export
 * @interface FetchArgs
 */
export interface FetchArgs {
    url: string;
    options: any;
}
/**
 *
 * @export
 * @class BaseAPI
 */
export declare class BaseAPI {
    protected basePath: string;
    protected fetch: FetchAPI;
    protected configuration: Configuration;
    constructor(configuration?: Configuration, basePath?: string, fetch?: FetchAPI);
}
/**
 *
 * @export
 * @class RequiredError
 * @extends {Error}
 */
export declare class RequiredError extends Error {
    field: string;
    name: "RequiredError";
    constructor(field: string, msg?: string);
}
/**
 *
 * @export
 * @interface ModelsRestServerStatus
 */
export interface ModelsRestServerStatus {
    /**
     *
     * @type {number}
     * @memberof ModelsRestServerStatus
     */
    time: number;
    /**
     *
     * @type {string}
     * @memberof ModelsRestServerStatus
     */
    address: string;
}
/**
 *
 * @export
 * @interface ModelsRestWSBroadcastMessage
 */
export interface ModelsRestWSBroadcastMessage {
    /**
     *
     * @type {string}
     * @memberof ModelsRestWSBroadcastMessage
     */
    message: string;
}
/**
 *
 * @export
 * @interface ModelsRestWSCreateGame
 */
export interface ModelsRestWSCreateGame {
}
/**
 *
 * @export
 * @interface ModelsRestWSInboxMessage
 */
export interface ModelsRestWSInboxMessage {
    /**
     *
     * @type {string}
     * @memberof ModelsRestWSInboxMessage
     */
    message: string;
}
/**
 *
 * @export
 * @interface ModelsRestWSJoinGame
 */
export interface ModelsRestWSJoinGame {
    /**
     *
     * @type {string}
     * @memberof ModelsRestWSJoinGame
     */
    id: string;
}
/**
 *
 * @export
 * @interface ModelsRestWSPing
 */
export interface ModelsRestWSPing {
}
/**
 *
 * @export
 * @interface ModelsRestWSPongMessage
 */
export interface ModelsRestWSPongMessage {
    /**
     *
     * @type {boolean}
     * @memberof ModelsRestWSPongMessage
     */
    success: boolean;
}
/**
 *
 * @export
 * @interface WSBroadcastMessage
 */
export interface WSBroadcastMessage {
}
/**
 *
 * @export
 * @interface WSCreateGame
 */
export interface WSCreateGame {
}
/**
 *
 * @export
 * @interface WSInboxMessage
 */
export interface WSInboxMessage {
}
/**
 *
 * @export
 * @interface WSJoinGame
 */
export interface WSJoinGame {
}
/**
 *
 * @export
 * @interface WSMessage
 */
export interface WSMessage {
    /**
     *
     * @type {WSMessageType}
     * @memberof WSMessage
     */
    msgType?: WSMessageType;
    /**
     *
     * @type {WSBroadcastMessage | WSCreateGame | WSJoinGame | WSInboxMessage}
     * @memberof WSMessage
     */
    data?: WSBroadcastMessage | WSCreateGame | WSJoinGame | WSInboxMessage;
}
/**
 *
 * @export
 * @enum {string}
 */
export declare enum WSMessageType {
    CreateGame,
    JoinGame,
    BroadcastMessage,
    InboxMessage,
    PingMessage,
    PongMessage
}
/**
 *
 * @export
 * @interface WSPing
 */
export interface WSPing {
}
/**
 *
 * @export
 * @interface WSPongMessage
 */
export interface WSPongMessage {
}
/**
 * RoutesApi - fetch parameter creator
 * @export
 */
export declare const RoutesApiFetchParamCreator: (configuration?: Configuration) => {
    /**
     * Returns the time and ip address of the server
     * @summary Gets status of server
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     */
    status(options?: any): FetchArgs;
};
/**
 * RoutesApi - functional programming interface
 * @export
 */
export declare const RoutesApiFp: (configuration?: Configuration) => {
    /**
     * Returns the time and ip address of the server
     * @summary Gets status of server
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     */
    status(options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<ModelsRestServerStatus>;
};
/**
 * RoutesApi - factory interface
 * @export
 */
export declare const RoutesApiFactory: (configuration?: Configuration, fetch?: FetchAPI, basePath?: string) => {
    /**
     * Returns the time and ip address of the server
     * @summary Gets status of server
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     */
    status(options?: any): Promise<ModelsRestServerStatus>;
};
/**
 * RoutesApi - object-oriented interface
 * @export
 * @class RoutesApi
 * @extends {BaseAPI}
 */
export declare class RoutesApi extends BaseAPI {
    /**
     * Returns the time and ip address of the server
     * @summary Gets status of server
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof RoutesApi
     */
    status(options?: any): Promise<ModelsRestServerStatus>;
}