package domain;

import java.util.ArrayList;
import java.util.List;

public class Player
{
	private String lastName;

	private String firstName;

	private String nickName;

	private String email;

	private Handler handler;

	private String club;

	private int age;

	private List<Double> skillsList = new ArrayList<>();

	private Gender gender;

	private boolean real = true;

	/**
	 * Default constructor
	 */
	public Player()
	{
	}

	public Player(String nickname)
	{
		this.nickName = nickname;
	}

	public Player(boolean real)
	{
		this.real = real;
		this.club = "not a player";
	}

	public boolean isReal()
	{
		return this.real;
	}

	public void setReal(boolean real)
	{
		this.real = real;
	}

	public double getAge()
	{
		return this.age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	public String getLastName()
	{
		return this.lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getNickName()
	{
		return this.nickName;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public Handler getHandler()
	{
		return this.handler;
	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	public String getClub()
	{
		return this.club;
	}

	public void setClub(String club)
	{
		this.club = club;
	}

	public Gender getGender()
	{
		return this.gender;
	}

	public void setGender(Gender gender)
	{
		this.gender = gender;
	}

	public List<Double> getSkillsList()
	{
		return this.skillsList;
	}

	public void setSkillsList(List<Double> skillsList)
	{
		this.skillsList = skillsList;
	}

	public List<Double> getSportScores(List<Double> expectedScores)
	{
		List<Double> scores = new ArrayList<>();
		for (int i = 0; i < this.skillsList.size(); i++)
		{
			scores.add(this.score(this.skillsList.get(i), expectedScores.get(i)));
		}
		return scores;
	}

	private double score(double value, double expected)
	{
		return Math.abs(value - expected);
	}

	@Override
	public String toString()
	{
		if (!this.isReal())
		{
			return "";
		}
		double sport = skillsList.stream().mapToDouble(Double::doubleValue).sum() / skillsList.size();
		return String.format("%s (%s %s) [%s](%s) score %.2f - %s", nickName, firstName, lastName, gender, handler,
			sport, club);
	}

	public enum Handler
	{
		YES, MAYBE, NO
	}

	public enum Gender
	{
		HOMME, FEMME
	}
}
