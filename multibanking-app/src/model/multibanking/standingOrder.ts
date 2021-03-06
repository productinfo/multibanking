/**
 * Multibanking REST Api
 * Use a bank code (blz) ending with X00 000 00 like 300 000 00 to run aggainst the mock backend. Find the mock backend at ${hostname}:10010
 *
 * OpenAPI spec version: 4.1.2-SNAPSHOT
 * Contact: age@adorsys.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { BankAccount } from './bankAccount';


/**
 * Standing order
 */
export interface StandingOrder {
    accountId?: string;
    amount?: number;
    cycle?: StandingOrder.CycleEnum;
    executionDay?: number;
    firstExecutionDate?: string;
    id?: string;
    lastExecutionDate?: string;
    orderId?: string;
    otherAccount?: BankAccount;
    usage?: string;
    userId?: string;
}
export namespace StandingOrder {
    export type CycleEnum = 'WEEKLY' | 'MONTHLY' | 'TWO_MONTHLY' | 'QUARTERLY' | 'HALF_YEARLY' | 'YEARLY' | 'INVALID';
    export const CycleEnum = {
        WEEKLY: 'WEEKLY' as CycleEnum,
        MONTHLY: 'MONTHLY' as CycleEnum,
        TWOMONTHLY: 'TWO_MONTHLY' as CycleEnum,
        QUARTERLY: 'QUARTERLY' as CycleEnum,
        HALFYEARLY: 'HALF_YEARLY' as CycleEnum,
        YEARLY: 'YEARLY' as CycleEnum,
        INVALID: 'INVALID' as CycleEnum
    }
}
