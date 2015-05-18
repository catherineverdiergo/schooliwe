package com.schooliwe.agenda;

import java.util.Comparator;

import com.schooliwe.lesson.Lesson;

public class LessonComparator implements Comparator<Lesson> {

	public int compare(Lesson o1, Lesson o2) {
		return (int)(o1.getDate().getTime()-o2.getDate().getTime());
	}

}
