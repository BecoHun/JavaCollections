import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

enum Skill {
    JAVA,
    DATABASE,
    SPRING,
    TESTING_TOOLS,
    AWS;
}

enum Position {
    DEVELOPER,
    KEY_DEVELOPER,
    TESTER;
}

enum Level {
    A1, A2, A3
}

class Role {
    private final Level level;
    private final Position position;
    private final Set<Skill> skills;

    public Role(Position position, Level level, Skill... skills) {
        this.position = position;
        this.level = level;
        this.skills = EnumSet.noneOf(Skill.class);

        Collections.addAll(this.skills, skills);
    }

    public Position getPosition() {
        return position;
    }

    public Level getLevel() {
        return level;
    }

    public Set<Skill> getSkills() {
        return skills;
    }
}

class Member {
    private final String name;
    private final Level level;
    private final Set<Skill> skills;

    public Member(String name, Level level, Skill... skills) {
        this.name = name;
        this.level = level;
        this.skills = EnumSet.noneOf(Skill.class);

        Collections.addAll(this.skills, skills);
    }

    public String getName() {
        return name;
    }

    public Level getLevel() {
        return level;
    }

    public Set<Skill> getSkills() {
        return skills;
    }
}

class Project {
    private final List<Role> roles;

    private static class Entry {
        private final Level level;
        private final Skill skill;

        public Entry(Level level, Skill skill) {
            this.level = level;
            this.skill = skill;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof Entry other)) {
                return false;
            }

            return level == other.level && skill == other.skill;
        }

        @Override
        public int hashCode() {
            int result = level.hashCode();
            result = 31 * result + skill.hashCode();
            return result;
        }
    }

    public Project(Role... roles) {
        this.roles = new ArrayList<>();

        Collections.addAll(this.roles, roles);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public int getConformity(Set<Member> team) {
        List<Entry> projectEntries = new ArrayList<>();
        List<Entry> teamEntries = new ArrayList<>();

        for (Role role : roles) {
            for (Skill skill : role.getSkills()) {
                projectEntries.add(new Entry(role.getLevel(), skill));
            }
        }

        int originalSize = projectEntries.size();

        if (originalSize == 0) {
            return 0;
        }

        for (Member member : team) {
            for (Skill skill : member.getSkills()) {
                teamEntries.add(new Entry(member.getLevel(), skill));
            }
        }

        for (int i = 0; i < projectEntries.size(); i++) {
            Entry projectEntry = projectEntries.get(i);

            int teamIndex = teamEntries.indexOf(projectEntry);

            if (teamIndex >= 0) {
                projectEntries.remove(i);
                teamEntries.remove(teamIndex);
                i--;
            }
        }

        return (originalSize - projectEntries.size()) * 100 / originalSize;
    }
}

public class ProjectSkills {

    public static void main(String[] args) {
        System.out.println("=== MemberTest futtatása ===");
        runMemberTests();

        System.out.println("\n=== RoleTest futtatása ===");
        runRoleTests();

        System.out.println("\n=== ProjectTest futtatása ===");
        runProjectTests();
    }

    private static void assertEquals(Object expected, Object actual, String testName) {
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            System.out.println("  [OK] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName + " - Elvárt: " + expected + ", de a kapott: " + actual);
        }
    }

    private static void runMemberTests() {
        // test1
        Member m1 = new Member("Name1", Level.A1, Skill.JAVA);
        assertEquals(EnumSet.class, m1.getSkills().getClass().getSuperclass(), "MemberTest.test1");

        // test2
        Member m2 = new Member("Name2", Level.A1, Skill.JAVA, Skill.DATABASE, Skill.SPRING);
        assertEquals(EnumSet.class, m2.getSkills().getClass().getSuperclass(), "MemberTest.test2");
    }

    private static void runRoleTests() {
        // test1
        Role r1 = new Role(Position.DEVELOPER, Level.A1, Skill.JAVA);
        assertEquals(EnumSet.class, r1.getSkills().getClass().getSuperclass(), "RoleTest.test1");

        // test2
        Role r2 = new Role(Position.DEVELOPER, Level.A1, Skill.JAVA, Skill.DATABASE, Skill.SPRING);
        assertEquals(EnumSet.class, r2.getSkills().getClass().getSuperclass(), "RoleTest.test2");
    }

    private static void runProjectTests() {
        // test1
        Project project1 = new Project(
            new Role(Position.DEVELOPER, Level.A1, Skill.JAVA, Skill.DATABASE),
            new Role(Position.KEY_DEVELOPER, Level.A2, Skill.JAVA, Skill.DATABASE, Skill.SPRING),
            new Role(Position.TESTER, Level.A3, Skill.TESTING_TOOLS, Skill.AWS),
            new Role(Position.TESTER, Level.A3, Skill.AWS)
        );
        Set<Member> team1 = new HashSet<>(Arrays.asList(
            new Member("Name1", Level.A1, Skill.JAVA, Skill.DATABASE),
            new Member("Name2", Level.A2, Skill.JAVA, Skill.DATABASE, Skill.SPRING),
            new Member("Name3", Level.A3, Skill.TESTING_TOOLS, Skill.AWS),
            new Member("Name4", Level.A3, Skill.TESTING_TOOLS)
        ));
        assertEquals(87, project1.getConformity(team1), "ProjectTest.test1");

        // test2
        Project project2 = new Project(
            new Role(Position.DEVELOPER, Level.A1, Skill.JAVA, Skill.DATABASE),
            new Role(Position.TESTER, Level.A3, Skill.TESTING_TOOLS, Skill.AWS),
            new Role(Position.TESTER, Level.A3, Skill.AWS)
        );
        Set<Member> team2 = new HashSet<>(Arrays.asList(
            new Member("Name1", Level.A1, Skill.JAVA, Skill.DATABASE),
            new Member("Name2", Level.A2, Skill.JAVA, Skill.DATABASE, Skill.SPRING),
            new Member("Name3", Level.A3, Skill.TESTING_TOOLS, Skill.AWS)
        ));
        assertEquals(80, project2.getConformity(team2), "ProjectTest.test2");

        // test3
        Project project3 = new Project(
            new Role(Position.DEVELOPER, Level.A1, Skill.JAVA),
            new Role(Position.KEY_DEVELOPER, Level.A2, Skill.JAVA),
            new Role(Position.TESTER, Level.A3, Skill.TESTING_TOOLS),
            new Role(Position.TESTER, Level.A3, Skill.AWS)
        );
        Set<Member> team3 = new HashSet<>(Arrays.asList(
            new Member("Name1", Level.A1, Skill.JAVA, Skill.DATABASE),
            new Member("Name2", Level.A2, Skill.JAVA, Skill.DATABASE),
            new Member("Name4", Level.A3, Skill.TESTING_TOOLS)
        ));
        assertEquals(75, project3.getConformity(team3), "ProjectTest.test3");

        // test4
        Project project4 = new Project(new Role(Position.DEVELOPER, Level.A1, Skill.JAVA));
        Set<Member> team4 = new HashSet<>(Arrays.asList(new Member("Name1", Level.A1, Skill.JAVA, Skill.JAVA)));
        assertEquals(100, project4.getConformity(team4), "ProjectTest.test4");

        // test5
        Project project5 = new Project(new Role(Position.DEVELOPER, Level.A1, Skill.JAVA));
        Set<Member> team5 = new HashSet<>();
        assertEquals(0, project5.getConformity(team5), "ProjectTest.test5");

        // test6
        Project project6 = new Project(
            new Role(Position.DEVELOPER, Level.A1, Skill.JAVA, Skill.DATABASE),
            new Role(Position.TESTER, Level.A3, Skill.TESTING_TOOLS, Skill.AWS),
            new Role(Position.TESTER, Level.A3, Skill.AWS)
        );
        Set<Member> team6 = new HashSet<>(Arrays.asList(
            new Member("Name1", Level.A1, Skill.JAVA, Skill.DATABASE),
            new Member("Name4", Level.A3, Skill.TESTING_TOOLS)
        ));
        assertEquals(60, project6.getConformity(team6), "ProjectTest.test6");
    }
}

/*
This code calculates a team-project compliance (conformity) metric.
It measures as a percentage how well the competencies of the team members cover the requirements of the project's expected roles.
Key Components and Data Structures
Skill, Position, Level (enums):
Define the acquirable skills, positions, and experience levels.
Role and Member
Role describes the required level (Level) and skills (Set<Skill>) for a given project position.
Member describes a team member's level and existing skills.
Both classes use EnumSet for efficient skill storage.
Entry (inner class in Project)
Represents an elemental <Level, Skill> pair.
This is the basic unit of calculation.
Core Logic (Project.getConformity)
Compliance is calculated through pair matching and removal.
Decomposing project requirements.
The code iterates through the roles assigned to the project (Role) and generates a <Level, Skill> pair for every required skill into the projectEntries list.
Decomposing team capabilities.
Similarly, it iterates through the team members (Member) and builds the teamEntries list from the members levels and skills.
Matching and deduction.
A loop iterates through the project requirements (projectEntries) one by one.
If an exact matching <Level, Skill> pair is found in the team's list (teamEntries), that matched pair is removed from both lists.
This ensures that a team member's skill at a specific level can only fulfill a single project requirement (preventing double counting).
Percentage result.
It divides the number of fulfilled requirements by the original number of requirements.
*/

