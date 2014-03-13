package org.genericsystem.jsf.example.initialisation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.genericsystem.cdi.event.EventLauncher.AfterGenericSystemStarts;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Attributes.Created;
import org.genericsystem.jsf.example.structure.Relations.IssueAssigneeRelation;
import org.genericsystem.jsf.example.structure.Relations.IssuePrioriyRelation;
import org.genericsystem.jsf.example.structure.Relations.IssueReporterRelation;
import org.genericsystem.jsf.example.structure.Relations.IssueStatusesRelation;
import org.genericsystem.jsf.example.structure.Types.Issues;
import org.genericsystem.jsf.example.structure.Types.Priorities;
import org.genericsystem.jsf.example.structure.Types.Statutes;
import org.genericsystem.jsf.example.structure.Types.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InitializationScript {

	protected static Logger log = LoggerFactory.getLogger(InitializationScript.class);

	@Inject
	private Engine engine;

	public void init(@Observes AfterGenericSystemStarts event) {

		Cache cache = engine.newCache().start();
		/**
		 * Issues
		 */
		Type issues = cache.find(Issues.class);
		Generic bug1 = issues.setInstance("bug1");
		Generic bug2 = issues.setInstance("bug2");
		Generic bug3 = issues.setInstance("bug3");
		Generic bug4 = issues.setInstance("bug4");
		/**
		 * Status
		 */
		Type status = cache.find(Statutes.class);
		Generic statusOpen = status.setInstance("Open");
		Generic statusProgress = status.setInstance("Coding in Progress");
		Generic statusResolved = status.setInstance("Resolved");
		Generic statusClosed = status.setInstance("Closed");
		/**
		 * User
		 */
		Type user = cache.find(Users.class);
		Generic user1 = user.setInstance("Charle");
		Generic user2 = user.setInstance("Ahmed");
		Generic user3 = user.setInstance("Mikael");
		Generic user4 = user.setInstance("Nicolas");

		/**
		 * Created
		 */
		Attribute created = cache.find(Created.class);

		/**
		 * Priority
		 */

		Type priority = cache.find(Priorities.class);
		Generic major = priority.getAllInstances().get(0);
		Generic minor = priority.getAllInstances().get(1);

		/**
		 * Relation
		 */

		Relation relationIssueReporter = cache.find(IssueReporterRelation.class);
		Relation relationIssueAssignee = cache.find(IssueAssigneeRelation.class);
		Relation relationIssueStatus = cache.find(IssueStatusesRelation.class);
		Relation relationIssuePriority = cache.find(IssuePrioriyRelation.class);

		/**
		 * Issue 1
		 */
		bug1.setLink(relationIssueAssignee, "relationIssueAssignee", user1);
		bug1.setLink(relationIssueStatus, "relationIssueStatus", statusOpen);
		bug1.setLink(relationIssueReporter, "RelationIssueReporter", user2);
		bug1.setLink(relationIssuePriority, "relationIssuePriority", major);
		bug1.addValue(created, "19/03/2014");

		/**
		 * issue 2
		 */
		bug2.setLink(relationIssueAssignee, "relationIssueAssignee1", user2);
		bug2.setLink(relationIssueStatus, "relationIssueStatus1", statusResolved);
		bug2.setLink(relationIssueReporter, "RelationIssueReporter1", user4);
		bug2.setLink(relationIssuePriority, "relationIssuePriority1", minor);
		bug2.addValue(created, "12/03/2014");

		/**
		 * issue 3
		 */
		bug3.setLink(relationIssueAssignee, "relationIssueAssignee2", user1);
		bug3.setLink(relationIssueStatus, "relationIssueStatus2", statusClosed);
		bug3.setLink(relationIssueReporter, "RelationIssueReporter2", user2);
		bug3.setLink(relationIssuePriority, "relationIssuePriority2", minor);
		bug3.addValue(created, "12/10/2014");

		/**
		 * issue4
		 */
		bug4.setLink(relationIssueAssignee, "relationIssueAssignee3", user3);
		bug4.setLink(relationIssueStatus, "relationIssueStatus3", statusProgress);
		bug4.setLink(relationIssueReporter, "RelationIssueReporter3", user1);
		bug4.setLink(relationIssuePriority, "relationIssuePriority3", minor);
		bug4.addValue(created, "11/09/2014");

		cache.flush();
	}
}
