/*
 *                               This program is free software: you can redistribute it and/or modify
 *                                it under the terms of the GNU General Public License as published by
 *                                the Free Software Foundation, version 3 of the License.
 *
 *                                This program is distributed in the hope that it will be useful,
 *                                but WITHOUT ANY WARRANTY; without even the implied warranty of
 *                                MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *                                GNU General Public License for more details.
 *
 *                                You should have received a copy of the GNU General Public License
 *                                along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *                                Copyright (c) 2019 Colin Redman
 */

package org.debatetool.io.accounts;

public class DBLockResponse {
    private final String message;
    private final ResultType resultType;

    public DBLockResponse(String message, ResultType resultType) {
        this.message = message;
        this.resultType = resultType;
    }

    public String getMessage() {
        return message;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public enum ResultType {SUCCESS, FAILURE_LOCKEDBY}


}
