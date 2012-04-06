/**
 * 
 */
package com.github.cthulhu666.drools.utils;

import java.util.List;

import org.drools.command.Command;

/**
 * @author Jakub Głuszecki
 *
 */
public interface DroolsCallback {
	
	Object execute(List<Command> commands);

}
